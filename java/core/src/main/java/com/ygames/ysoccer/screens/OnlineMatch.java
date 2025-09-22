package com.ygames.ysoccer.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.esotericsoftware.kryonet.Client;
import com.ygames.ysoccer.framework.Assets;
import com.ygames.ysoccer.framework.GLGame;
import com.ygames.ysoccer.framework.GLScreen;
import com.ygames.ysoccer.match.Match;
import com.ygames.ysoccer.match.MatchCamera;
import com.ygames.ysoccer.match.MatchRenderer;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.network.dto.MatchSetupDto;
import com.ygames.ysoccer.network.mappers.MatchMapper;

import static com.badlogic.gdx.Gdx.gl;
import static com.ygames.ysoccer.match.Match.AWAY;
import static com.ygames.ysoccer.match.Match.HOME;

public class OnlineMatch extends GLScreen {

    Match match;

    private MatchRenderer matchRenderer;

    int zoom = 100;

    public OnlineMatch(GLGame game, Client client) {
        super(game);
        usesMouse = false;
    }

    public void setup(MatchSetupDto matchSetupDto) {
        match = MatchMapper.fromDto(matchSetupDto.matchDto);
        match.setActionCamera(new MatchCamera(match));
        matchRenderer = new MatchRenderer(game.glGraphics, match);
        Assets.loadStadium(match.getSettings());
        Assets.loadCrowd(match.team[Match.HOME]);
        Assets.loadBall(match.getSettings());
        Assets.loadCornerFlags(match.getSettings());
        for (int t = HOME; t <= AWAY; t++) {
            Assets.loadCoach(match.team[t]);
            int len = match.team[t].lineup.size();
            for (int i = 0; i < len; i++) {
                Player player = match.team[t].lineup.get(i);
                if (player.role == Player.Role.GOALKEEPER) {
                    Assets.loadKeeper(player);
                } else {
                    Assets.loadPlayer(player, match.team[t].kits.get(match.team[t].kitIndex));
                }
                Assets.loadHair(player);
            }
        }
        game.disableMouse();
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        matchRenderer.render();
    }

    private void renderBackground() {
        gl.glEnable(GL20.GL_BLEND);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.setToOrtho(true, Gdx.graphics.getWidth() * 100f / zoom, Gdx.graphics.getHeight() * 100f / zoom);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.disableBlending();
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                batch.draw(Assets.stadium[r][c], 512 * c, 512 * r);
            }
        }
        batch.enableBlending();
        batch.end();
    }
}
