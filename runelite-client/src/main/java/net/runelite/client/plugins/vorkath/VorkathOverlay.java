package net.runelite.client.plugins.vorkath;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Skill;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

public class VorkathOverlay extends Overlay {

    private static final Color COLOR_ICON_BACKGROUND = new Color(0, 0, 0, 128);
    private static final Color COLOR_ICON_BORDER = new Color(0, 0, 0, 255);
    private static final Color COLOR_ICON_BORDER_FILL = new Color(219, 175, 0, 255);
    private static final int OVERLAY_ICON_DISTANCE = 30;
    private static final int OVERLAY_ICON_MARGIN = 1;

    private Client client;
    private VorkathPlugin plugin;

    @Inject
    private SkillIconManager iconManager;

    @Inject
    public VorkathOverlay(Client client, VorkathPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
    }

    private BufferedImage getIcon(Vorkath.AttackStyle attackStyle)
    {
        switch (attackStyle)
        {
            case MAGERANGE: return iconManager.getSkillImage(Skill.RANGED);
            case ICE: return iconManager.getSkillImage(Skill.MAGIC);
            case ACID: return iconManager.getSkillImage(Skill.AGILITY);
        }
        return null;
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (plugin.getSpawn() != null) {
            ZombifiedSpawn spawn = plugin.getSpawn();
            OverlayUtil.renderTileOverlay(client, graphics, spawn.getNpc().getLocalLocation(), getIcon(Vorkath.AttackStyle.ICE), Color.green);
        }

        if(plugin.getVorkath() != null){
            Vorkath vorkath = plugin.getVorkath();

            LocalPoint localLocation = vorkath.getNpc().getLocalLocation();
            if(localLocation != null){
                Point point = Perspective.localToCanvas(client, localLocation, client.getPlane(), vorkath.getNpc().getLogicalHeight() + 16);
                if (point != null) {
                    point = new Point(point.getX(), point.getY());

                    BufferedImage icon = null;
                    if(vorkath.getPhase() == 0){
                        icon = getIcon(Vorkath.AttackStyle.MAGERANGE);
                    } else if(vorkath.getPhase() == 1) {
                        icon = getIcon(Vorkath.AttackStyle.ACID);
                    } else if(vorkath.getPhase() == 2) {
                        icon = getIcon(Vorkath.AttackStyle.ICE);
                    }

                    int totalWidth = icon.getWidth() * OVERLAY_ICON_MARGIN;
                    int bgPadding = 8;
                    int currentPosX = 0;

                    graphics.setStroke(new BasicStroke(2));
                    graphics.setColor(COLOR_ICON_BACKGROUND);
                    graphics.fillOval(
                            point.getX() - totalWidth / 2 + currentPosX - bgPadding,
                            point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
                            icon.getWidth() + bgPadding * 2,
                            icon.getHeight() + bgPadding * 2);

                    graphics.setColor(COLOR_ICON_BORDER);
                    graphics.drawOval(
                            point.getX() - totalWidth / 2 + currentPosX - bgPadding,
                            point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
                            icon.getWidth() + bgPadding * 2,
                            icon.getHeight() + bgPadding * 2);

                    graphics.drawImage(
                            icon,
                            point.getX() - totalWidth / 2 + currentPosX,
                            point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE,
                            null);

                    graphics.setColor(COLOR_ICON_BORDER_FILL);
                    Arc2D.Double arc = new Arc2D.Double(
                            point.getX() - totalWidth / 2 + currentPosX - bgPadding,
                            point.getY() - icon.getHeight() / 2 - OVERLAY_ICON_DISTANCE - bgPadding,
                            icon.getWidth() + bgPadding * 2,
                            icon.getHeight() + bgPadding * 2,
                            90.0,
                            -360.0 * (vorkath.ATTACKS_PER_SWITCH -
                                    vorkath.getAttacksUntilSwitch()) / vorkath.ATTACKS_PER_SWITCH,
                            Arc2D.OPEN);
                    graphics.draw(arc);
                }
            }
        }

        return null;
    }

}