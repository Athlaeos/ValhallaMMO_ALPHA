package me.athlaeos.valhallammo.placeholder.placeholders.mining;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class BlastTntFortuneLevel extends Placeholder {
    private final String fortune;
    private final String silk;
    private final String normal;

    public BlastTntFortuneLevel(String placeholder) {
        super(placeholder);
        this.fortune = TranslationManager.getInstance().getTranslation("blast_tnt_fortune");
        this.silk = TranslationManager.getInstance().getTranslation("blast_tnt_silk");
        this.normal = TranslationManager.getInstance().getTranslation("blast_tnt_none");
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "MINING");
        if (profile != null){
            if (profile instanceof MiningProfile){
                int level = ((MiningProfile) profile).getExplosionFortuneLevel();
                return s.replace(this.placeholder, (level < 0) ? silk : (level == 0) ? normal : fortune.replace("%level%", "" + level));
            }
        }
        return s;
    }
}
