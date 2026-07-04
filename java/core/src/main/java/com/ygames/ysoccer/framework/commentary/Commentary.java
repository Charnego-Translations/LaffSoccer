package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.audio.Sound;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.match.Match;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ygames.ysoccer.framework.GLGame.LogType.COMMENTARY;

/**
 * Singleton that will manage commentaries in-match
 */
public class Commentary {

    private static final String THREAD_NAME = "Commentary-thread";

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

        Comment incoming = elements[0];

        if (!QueuePolicy.shouldEnqueue(incoming, queueLength, playing != null)) {
            GLGame.debug(COMMENTARY, elements, "Commentary not queued: queue too long: " + queueLength);
            return;
        }

        Comment queuedNext = queue.isEmpty() ? null : queue.peek()[0];

        // A comment with greater priority comes (or queue is very long)
        if (QueuePolicy.shouldClearQueue(incoming, playing, queuedNext, queueLength))  {
            GLGame.debug(COMMENTARY, elements, "Queue clear and commentary pushed immediately");
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

    /**
     * Prepares and enqueue end game comment
     */
    public void enqueueMatchEndComment(Match match) {
        Comment[] resultComment = CommentBuilder.buildResultComment(match);
        if (resultComment != null) {
            enqueueComment(resultComment);
        }
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
                enqueueComment(CommentBuilder.getComment(CommonCommentType.CHITCHAT, CommentPriority.CHITCHAT));
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
