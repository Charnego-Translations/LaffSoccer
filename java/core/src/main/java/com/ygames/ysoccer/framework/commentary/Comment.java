package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.audio.Sound;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A comment element
 */
@Getter
@AllArgsConstructor
public class Comment {



    public final CommentPriority commentPriority;
    public final Sound sound;

}
