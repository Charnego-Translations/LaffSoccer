package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.audio.Sound;
import com.ygames.ysoccer.framework.EMath;
import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.SoundManager;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchStats;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ygames.ysoccer.framework.EMath.randomPick;
import static com.ygames.ysoccer.framework.GLGame.LogType.COMMENTARY;

/**
 * Builds the comment lines for a given situation ("what to say").
 * Sequencing and playback of the built comments is {@link Commentary}'s job, not this class's.
 */
public final class CommentBuilder {

    private static final int MANY_GOALS_THRESHOLD = 5;
    private static final int VIOLENT_MATCH_FOULS_THRESHOLD = 15;
    private static final int TRASHING_GOAL_DIFFERENCE = 3;

    private CommentBuilder() {
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

        if (home.goals + away.goals > MANY_GOALS_THRESHOLD) {
            sounds.add(SoundManager.manyGoalsHalfTime);
        }
        if (home.foulsConceded + away.foulsConceded > VIOLENT_MATCH_FOULS_THRESHOLD) {
            sounds.add(SoundManager.violentMatch);
        }
        if (home.goals + away.goals == 0) {
            sounds.add(SoundManager.noGoalsHalfTime);
        }
        if (home.goals + TRASHING_GOAL_DIFFERENCE < away.goals) {
            sounds.add(SoundManager.awayTeamTrashing);
        }
        if (home.goals > away.goals + TRASHING_GOAL_DIFFERENCE) {
            sounds.add(SoundManager.localTeamTrashing);
        }

        if (!sounds.isEmpty()) {
            return new Comment[] {new Comment(CommentPriority.HIGH, randomPick(sounds))};
        }
        return null;
    }
}
