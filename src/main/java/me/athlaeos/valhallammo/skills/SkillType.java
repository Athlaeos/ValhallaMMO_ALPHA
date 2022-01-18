package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.skills.smithing.dom.SmithingProfile;
import org.bukkit.NamespacedKey;

public enum SkillType {
    SMITHING(new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_smithing"), new SmithingProfile(null)),
    ENCHANTING(null, null),
    ALCHEMY(null, null),
    FARMING(null, null),
    MINING(null, null),
    WOODCUTTING(null, null),
    TRADING(null, null),
    WEAPONS_LIGHT(null, null),
    WEAPONS_HEAVY(null, null),
    UNARMED(null, null),
    ARMOR_LIGHT(null, null),
    ARMOR_HEAVY(null, null),
    ARCHERY(null, null),
    ACROBATICS(null, null),
    ACCOUNT(new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_account"), new AccountProfile(null));

    private final NamespacedKey key;
    private final Profile type;

    SkillType(NamespacedKey key, Profile type){
        this.key = key;
        this.type = type;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public Profile getType() {
        return type;
    }
}
