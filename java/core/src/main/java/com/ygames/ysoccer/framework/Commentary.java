package com.ygames.ysoccer.framework;

import com.badlogic.gdx.audio.Sound;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchStats;
import lombok.AllArgsConstructor;
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
import java.util.Timer;
import java.util.TimerTask;

import static com.ygames.ysoccer.framework.Assets.RANDOM;
import static com.ygames.ysoccer.framework.EMath.randomPick;
import static com.ygames.ysoccer.framework.GLGame.LogType.COMMENTARY;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final String THREAD_NAME = "Commentary-thread";
    private static final float MAX_QUEUE = 2.0f;
    private static final float SHORT_QUEUE = 0.15f;

    public static final Commentary INSTANCE = new Commentary();
    @Getter
    @Setter
    private static boolean enabled = true;

    /**
     * A comment element
     */
    @Getter
    @AllArgsConstructor
    public static class Comment {

        @AllArgsConstructor
        public enum Priority {
            CHITCHAT(4), LOW(1), COMMON(2), HIGH(3), GOAL(5);
            private final int weight;
        }

        private final Priority priority;
        private final Sound sound;

    }

    /**
     * This is meant to be a singleton
     */
    private Commentary() {}

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

    private Timer timer;

    /**
     * Enqueue a comment
     * @param elements
     */
    public synchronized void enqueueComment(Comment... elements) {

        if (elements == null || elements.length == 0) {
            GLGame.debug(COMMENTARY, null, "Queued null comment");
            return;
        }

        if (queueLength > MAX_QUEUE && playing != null && playing.priority.weight > elements[0].priority.weight && elements[0].priority != Comment.Priority.CHITCHAT) {
            GLGame.debug(COMMENTARY, elements, "Commentary not queued: queue too long: " + queueLength);
            return;
        }

        // A comment with greater priority comes (or queue is very long)
        if ((playing != null && playing.priority.weight < elements[0].priority.weight && queueLength < SHORT_QUEUE || queueLength > MAX_QUEUE) && elements[0].priority != Comment.Priority.CHITCHAT)  {
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: is not chitchat? " + (elements[0].priority != Comment.Priority.CHITCHAT));
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: higher priority? " + (playing == null? "(not playing)" : playing.priority.weight < elements[0].priority.weight));
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately: short queue?" + (queueLength < SHORT_QUEUE));
            queue.clear();
            current.clear();
            queueLength = 0;
            since = 0L;
            if (lastSound != null) {
                lastSound.stop();
            }
        }

        for (Comment element : elements) {
            queueLength += FileUtils.soundDuration(element.getSound());
        }
        GLGame.debug(COMMENTARY, queueLength, "Queue length: " + queueLength);
        queue.add(elements);
    }

    /**
     * Prepares and enqueue end game comment
     */
    public void endGameComment(Match match) {
        enqueueComment(Commentary.getComment(SoundManager.CommonComment.CommonCommentType.MATCH_END, Comment.Priority.HIGH));
        Comment[] resultComment = buildResult(match);
        if (resultComment != null) {
            enqueueComment(resultComment);
        }
    }

    /**
     * Prepares a random comment of type and priority specified
     * @param type
     * @param priority
     * @return the composed comment
     */
    public static Comment[] getComment(SoundManager.CommonComment.CommonCommentType type, Comment.Priority priority) {

        GLGame.debug(COMMENTARY, priority, "Generating new comment: " + type);

        List<Comment> result = new ArrayList<>();
        result.add(new Comment(priority, SoundManager.CommonComment.pull(type)));
        if (RANDOM.nextInt(6) > 2) {
            Sound secSound = SoundManager.CommonComment.pullSecond(type);
            if (secSound != null) {
                result.add(new Comment(priority == Comment.Priority.HIGH ? Comment.Priority.COMMON : priority, SoundManager.CommonComment.pullSecond(type)));
            }
        }

        return result.toArray(new Comment[result.size()]);
    }

    /**
     * Builds a comment saying the result
     * @param match
     * @return
     */
    public static Comment[] buildResult(Match match) {
        Sound[] numbers = SoundManager.CommonComment.numbers;

        MatchStats home = match.stats[Match.HOME];
        MatchStats away = match.stats[Match.AWAY];
        Map<String, Assets.TeamCommentary> teams = Assets.TeamCommentary.teams;

        Assets.TeamCommentary homeName = teams.get(FileUtils.getTeamFromFile(match.team[Match.HOME].path));
        Assets.TeamCommentary awayName = teams.get(FileUtils.getTeamFromFile(match.team[Match.AWAY].path));

        if (numbers[(home.goals)] == null
            || numbers[(away.goals)] == null
            || homeName.teamName == null || awayName.teamName == null) {
            return null;
        }
        return new Comment[] {
                new Comment(Comment.Priority.HIGH, homeName.teamName),
                new Comment(Comment.Priority.HIGH, numbers[(home.goals)]),
                new Comment(Comment.Priority.HIGH, awayName.teamName),
                new Comment(Comment.Priority.HIGH, numbers[(away.goals)])
            };
    }

    /**
     * Builds a comment for half time
     * @param match
     * @return
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
            return new Comment[] {new Comment(Comment.Priority.LOW, randomPick(sounds))};
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

        lastLength = FileUtils.soundDuration(target.getSound());
        queueLength -= lastLength;
        since = System.currentTimeMillis();

        playing = target;
        try {
            if (playing.getSound() != null) {
                playing.getSound().play();
                lastSound = playing.getSound();
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

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run()  {
                tick();
            }
        }, 1, 50);
    }

    public synchronized void tick() {

        Thread.currentThread().setName(THREAD_NAME);

        long now = System.currentTimeMillis();

        if (now - lastChitChat > 20000) {
            Random rnd = new Random();
            if (rnd.nextInt((int) EMath.max(1, (now - lastChitChat))) > 36000) {
                enqueueComment(getComment(SoundManager.CommonComment.CommonCommentType.CHITCHAT, Comment.Priority.CHITCHAT));
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

        timer.cancel();

        current.clear();
        queue.clear();

        if (playing != null) {
            playing.getSound().stop();
            playing = null;
        }

    }

}
