package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.audio.Sound;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.SoundManager;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchStats;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ygames.ysoccer.framework.EMath.randomPick;
import static com.ygames.ysoccer.framework.GLGame.LogType.COMMENTARY;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final String THREAD_NAME = "Commentary-thread";
    private static final float MAX_QUEUE = 3.0f;

    public static final Commentary INSTANCE = new Commentary();
    @Getter
    @Setter
    private static boolean enabled = true;

    /**
     * This is meant to be a singleton
     */
    private Commentary() {
    }

    /**
     * Queue of comments
     */
    private final Queue<Comment[]> queue = new LinkedList<>();

    /**
     * Currently playing comments
     */
    private final Queue<Comment> current = new LinkedList<>();

    /**
     * Current playing sound
     */
    private Comment playing = null;

    private long lastChitChat = System.currentTimeMillis();

    private long since = 0L;
    private float lastLength = 0F;
    private float queueLength = 0F;
    private Sound lastSound = null;

    private ScheduledExecutorService scheduler;

    /**
     * Enqueue a comment
     * @param elements Elements to enqueue
     */
    public synchronized void enqueueComment(Comment... elements) {

        if (elements == null || elements.length == 0) {
            GLGame.debug(COMMENTARY, null, "Queued null comment");
            return;
        }

        if (!shouldEnqueue(elements)) {
            GLGame.debug(COMMENTARY, elements, "Commentary not queued: queue too long: " + queueLength);
            return;
        }

        // A comment with greater priority comes (or queue is very long)
        if (shouldClearQueue(elements))  {
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: is not chitchat? " + (elements[0].commentPriority != CommentPriority.CHITCHAT));
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: higher priority? " + (playing == null? "(not playing)" : playing.commentPriority.weight < elements[0].commentPriority.weight));
            queue.clear();
            current.clear();
            queueLength = 0;
            since = 0L;
            if (lastSound != null) {
                lastSound.stop();
            }
        }

        for (Comment element : elements) {
            queueLength += FileUtils.soundDuration(element.sound);
        }
        GLGame.debug(COMMENTARY, queueLength, "Queue length: " + queueLength);
        queue.add(elements);
    }

    private boolean shouldEnqueue(Comment... elements) {
        return queueLength < MAX_QUEUE
            || playing == null
            || elements[0].commentPriority.weight >= CommentPriority.HIGH.weight;
    }

    private boolean shouldClearQueue(Comment... elements) {
        return (playing != null
            && elements[0].commentPriority.weight > playing.commentPriority.weight
            && !queue.isEmpty() && elements[0].commentPriority.weight > queue.peek()[0].commentPriority.weight
            && elements[0].commentPriority != CommentPriority.CHITCHAT
            && elements[0].commentPriority.weight > CommentPriority.HIGH.weight)
            || (playing != null && queueLength > MAX_QUEUE && elements[0].commentPriority.weight >= CommentPriority.HIGH.weight && playing.commentPriority.weight >= CommentPriority.HIGH.weight)
            || (playing != null && elements[0].commentPriority == CommentPriority.LOW && playing.commentPriority == CommentPriority.LOW);
    }

    /**
     * Prepares and enqueue end game comment
     */
    public void enqueueMatchEndComment(Match match) {
        Comment[] resultComment = buildResultComment(match);
        if (resultComment != null) {
            enqueueComment(resultComment);
        }
    }

    /**
     * Prepares a random comment of type and priority specified
     * @param type Common comment type
     * @param commentPriority Comment priority
     * @return the composed comment
     */
    public static Comment[] getComment(CommonCommentType type, CommentPriority commentPriority, Team homeTeam, Team offenderteam, Player player) {

        GLGame.debug(COMMENTARY, commentPriority, "Generating new comment: " + type);

        List<Comment> result = new ArrayList<>();
        for (Sound sound : CommonComment.pull(type, homeTeam, offenderteam, player)) {
            result.add(new Comment(commentPriority, sound));
        }
        EMath.oneIn(2.5f, () -> {
            Sentence commonComment = CommonComment.pullSecond(type);
            if (commonComment != null && commonComment.sound != null) {
                result.add(new Comment(commentPriority == CommentPriority.HIGH ? CommentPriority.COMMON : commentPriority, commonComment.sound));
            }
        });

        return result.toArray(new Comment[0]);
    }

    public static Comment[] getComment(CommonCommentType type, CommentPriority commentPriority) {
        return getComment(type, commentPriority, null, null, null);
    }

    /**
     * Builds a comment saying the result
     * @param match Match object
     * @return built comment
     */
    public static Comment[] buildResultComment(Match match) {
        Sound[] numbers = CommonComment.numbers;

        MatchStats home = match.stats[Match.HOME];
        MatchStats away = match.stats[Match.AWAY];
        Map<String, TeamCommentary> teams = TeamCommentary.teams;

        TeamCommentary homeName = teams.get(FileUtils.getTeamFromFile(match.team[Match.HOME].path));
        TeamCommentary awayName = teams.get(FileUtils.getTeamFromFile(match.team[Match.AWAY].path));

        if (numbers[(home.goals)] == null
            || numbers[(away.goals)] == null
            || homeName.teamName == null || awayName.teamName == null) {
            return null;
        }
        return new Comment[] {
                new Comment(CommentPriority.HIGH, homeName.teamName),
                new Comment(CommentPriority.HIGH, numbers[(home.goals)]),
                new Comment(CommentPriority.HIGH, awayName.teamName),
                new Comment(CommentPriority.HIGH, numbers[(away.goals)])
            };
    }

    /**
     * Builds a comment for half-time
     * @param match match object
     * @return built comment
     */
    public static Comment[] halfTime(Match match) {

        Set<Sound> sounds = new HashSet<>();

        MatchStats home = match.stats[Match.HOME];
        MatchStats away = match.stats[Match.AWAY];

        if (home.goals + away.goals > 5) {
            sounds.add(SoundManager.manyGoalsHalfTime);
        }
        if (home.foulsConceded + away.foulsConceded > 15) {
            sounds.add(SoundManager.violentMatch);
        }
        if (home.goals + away.goals == 0) {
            sounds.add(SoundManager.noGoalsHalfTime);
        }
        if (home.goals + 3 < away.goals) {
            sounds.add(SoundManager.awayTeamTrashing);
        }
        if (home.goals > away.goals + 3) {
            sounds.add(SoundManager.localTeamTrashing);
        }

        if (!sounds.isEmpty()) {
            return new Comment[] {new Comment(CommentPriority.HIGH, randomPick(sounds))};
        }
        return null;
    }

    /**
     * Pulls a comment from the queue and plays it
     * @return whether it did or not
     */
    private boolean pullAndPlay() {

        if (current.isEmpty() || !enabled) {
            return false;
        }

        Comment target = current.poll();

        GLGame.debug(COMMENTARY, target, "Pulling new comment: " + target);

        lastLength = FileUtils.soundDuration(target.sound);
        queueLength -= lastLength;
        since = System.currentTimeMillis();

        playing = target;
        try {
            if (playing.sound != null) {
                playing.sound.play();
                lastSound = playing.sound;
            }
        } catch (UnsatisfiedLinkError ex) {
            GLGame.debug(COMMENTARY, this, "Couldn't play comment: " + ex.getMessage());
        }

        return true;
    }

    /**
     * Awakens the commentary thread
     */
    public void wake() {

        GLGame.debug(COMMENTARY, this, "Waking commentary subsystem");
        lastChitChat = System.currentTimeMillis();

        since = System.currentTimeMillis();

        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(
            this::tick,
            1,
            50,
            TimeUnit.MILLISECONDS
        );
    }

    public synchronized void tick() {

        Thread.currentThread().setName(THREAD_NAME);

        long now = System.currentTimeMillis();

        if (now - lastChitChat > 50000) {
            if (Assets.RANDOM.nextInt((int) EMath.max(1, (now - lastChitChat))) > 36000) {
                enqueueComment(getComment(CommonCommentType.CHITCHAT, CommentPriority.CHITCHAT));
                lastChitChat = now;
            }
        }

        if (playing != null && now > since + ((long) (lastLength * 1000))) {
            playing = null;
        }

        if (playing == null) {
            if (pullAndPlay()) {
                return;
            }
        }

        if (!current.isEmpty() && playing == null) {
            playing = current.poll();
            return;
        }

        if (!queue.isEmpty() && current.isEmpty()) {
            Comment[] next = queue.poll();

            if (next != null) {
                current.addAll(Arrays.asList(next));
            }
        }

    }

    /**
     * Stops the commentary thread
     */
    public void stop() {

        GLGame.debug(COMMENTARY, this, "Stopping commentary subsystem");

        if (scheduler != null) {
            scheduler.shutdown();
        }

        current.clear();
        queue.clear();

        if (playing != null) {
            playing.sound.stop();
            playing = null;
        }

    }

}
