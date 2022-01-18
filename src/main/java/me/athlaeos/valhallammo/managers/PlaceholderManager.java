package me.athlaeos.valhallammo.placeholder;

import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.materials.MaterialClass;
import me.athlaeos.valhallammo.placeholder.placeholders.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderManager {
    private static PlaceholderManager manager = null;
    private final Map<String, Placeholder> placeholders = new HashMap<>();

    public static PlaceholderManager getInstance(){
        if (manager == null) manager = new PlaceholderManager();
        return manager;
    }

    public PlaceholderManager(){
        registerPlaceholder(new EXPCurrent("%account_exp_current%", SkillType.ACCOUNT));
        registerPlaceholder(new EXPCurrent("%smithing_exp_current%", SkillType.SMITHING));
        registerPlaceholder(new EXPCurrent("%acrobatics_exp_current%", SkillType.ACROBATICS));
        registerPlaceholder(new EXPCurrent("%alchemy_exp_current%", SkillType.ALCHEMY));
        registerPlaceholder(new EXPCurrent("%archery_exp_current%", SkillType.ARCHERY));
        registerPlaceholder(new EXPCurrent("%armor_heavy_exp_current%", SkillType.ARMOR_HEAVY));
        registerPlaceholder(new EXPCurrent("%armor_light_exp_current%", SkillType.ARMOR_LIGHT));
        registerPlaceholder(new EXPCurrent("%enchanting_exp_current%", SkillType.ENCHANTING));
        registerPlaceholder(new EXPCurrent("%farming_exp_current%", SkillType.FARMING));
        registerPlaceholder(new EXPCurrent("%mining_exp_current%", SkillType.MINING));
        registerPlaceholder(new EXPCurrent("%trading_exp_current%", SkillType.TRADING));
        registerPlaceholder(new EXPCurrent("%unarmed_exp_current%", SkillType.UNARMED));
        registerPlaceholder(new EXPCurrent("%weapons_heavy_exp_current%", SkillType.WEAPONS_HEAVY));
        registerPlaceholder(new EXPCurrent("%weapons_light_exp_current%", SkillType.WEAPONS_LIGHT));
        registerPlaceholder(new EXPCurrent("%woodcutting_exp_current%", SkillType.WOODCUTTING));

        registerPlaceholder(new EXPNextLevel("%account_exp_next%", SkillType.ACCOUNT));
        registerPlaceholder(new EXPNextLevel("%smithing_exp_next%", SkillType.SMITHING));
        registerPlaceholder(new EXPNextLevel("%acrobatics_exp_next%", SkillType.ACROBATICS));
        registerPlaceholder(new EXPNextLevel("%alchemy_exp_next%", SkillType.ALCHEMY));
        registerPlaceholder(new EXPNextLevel("%archery_exp_next%", SkillType.ARCHERY));
        registerPlaceholder(new EXPNextLevel("%armor_heavy_exp_next%", SkillType.ARMOR_HEAVY));
        registerPlaceholder(new EXPNextLevel("%armor_light_exp_next%", SkillType.ARMOR_LIGHT));
        registerPlaceholder(new EXPNextLevel("%enchanting_exp_next%", SkillType.ENCHANTING));
        registerPlaceholder(new EXPNextLevel("%farming_exp_next%", SkillType.FARMING));
        registerPlaceholder(new EXPNextLevel("%mining_exp_next%", SkillType.MINING));
        registerPlaceholder(new EXPNextLevel("%trading_exp_next%", SkillType.TRADING));
        registerPlaceholder(new EXPNextLevel("%unarmed_exp_next%", SkillType.UNARMED));
        registerPlaceholder(new EXPNextLevel("%weapons_heavy_exp_next%", SkillType.WEAPONS_HEAVY));
        registerPlaceholder(new EXPNextLevel("%weapons_light_exp_next%", SkillType.WEAPONS_LIGHT));
        registerPlaceholder(new EXPNextLevel("%woodcutting_exp_next%", SkillType.WOODCUTTING));

        registerPlaceholder(new EXPPercentProgress("%account_exp_percent%", SkillType.ACCOUNT));
        registerPlaceholder(new EXPPercentProgress("%smithing_exp_percent%", SkillType.SMITHING));
        registerPlaceholder(new EXPPercentProgress("%acrobatics_exp_percent%", SkillType.ACROBATICS));
        registerPlaceholder(new EXPPercentProgress("%alchemy_exp_percent%", SkillType.ALCHEMY));
        registerPlaceholder(new EXPPercentProgress("%archery_exp_percent%", SkillType.ARCHERY));
        registerPlaceholder(new EXPPercentProgress("%armor_heavy_exp_percent%", SkillType.ARMOR_HEAVY));
        registerPlaceholder(new EXPPercentProgress("%armor_light_exp_percent%", SkillType.ARMOR_LIGHT));
        registerPlaceholder(new EXPPercentProgress("%enchanting_exp_percent%", SkillType.ENCHANTING));
        registerPlaceholder(new EXPPercentProgress("%farming_exp_percent%", SkillType.FARMING));
        registerPlaceholder(new EXPPercentProgress("%mining_exp_percent%", SkillType.MINING));
        registerPlaceholder(new EXPPercentProgress("%trading_exp_percent%", SkillType.TRADING));
        registerPlaceholder(new EXPPercentProgress("%unarmed_exp_percent%", SkillType.UNARMED));
        registerPlaceholder(new EXPPercentProgress("%weapons_heavy_exp_percent%", SkillType.WEAPONS_HEAVY));
        registerPlaceholder(new EXPPercentProgress("%weapons_light_exp_percent%", SkillType.WEAPONS_LIGHT));
        registerPlaceholder(new EXPPercentProgress("%woodcutting_exp_percent%", SkillType.WOODCUTTING));

        registerPlaceholder(new EXPTotal("%account_exp_total%", SkillType.ACCOUNT));
        registerPlaceholder(new EXPTotal("%smithing_exp_total%", SkillType.SMITHING));
        registerPlaceholder(new EXPTotal("%acrobatics_exp_total%", SkillType.ACROBATICS));
        registerPlaceholder(new EXPTotal("%alchemy_exp_total%", SkillType.ALCHEMY));
        registerPlaceholder(new EXPTotal("%archery_exp_total%", SkillType.ARCHERY));
        registerPlaceholder(new EXPTotal("%armor_heavy_exp_total%", SkillType.ARMOR_HEAVY));
        registerPlaceholder(new EXPTotal("%armor_light_exp_total%", SkillType.ARMOR_LIGHT));
        registerPlaceholder(new EXPTotal("%enchanting_exp_total%", SkillType.ENCHANTING));
        registerPlaceholder(new EXPTotal("%farming_exp_total%", SkillType.FARMING));
        registerPlaceholder(new EXPTotal("%mining_exp_total%", SkillType.MINING));
        registerPlaceholder(new EXPTotal("%trading_exp_total%", SkillType.TRADING));
        registerPlaceholder(new EXPTotal("%unarmed_exp_total%", SkillType.UNARMED));
        registerPlaceholder(new EXPTotal("%weapons_heavy_exp_total%", SkillType.WEAPONS_HEAVY));
        registerPlaceholder(new EXPTotal("%weapons_light_exp_total%", SkillType.WEAPONS_LIGHT));
        registerPlaceholder(new EXPTotal("%woodcutting_exp_total%", SkillType.WOODCUTTING));

        registerPlaceholder(new LevelCurrent("%account_level%", SkillType.ACCOUNT));
        registerPlaceholder(new LevelCurrent("%smithing_level%", SkillType.SMITHING));
        registerPlaceholder(new LevelCurrent("%acrobatics_level%", SkillType.ACROBATICS));
        registerPlaceholder(new LevelCurrent("%alchemy_level%", SkillType.ALCHEMY));
        registerPlaceholder(new LevelCurrent("%archery_level%", SkillType.ARCHERY));
        registerPlaceholder(new LevelCurrent("%armor_heavy_level%", SkillType.ARMOR_HEAVY));
        registerPlaceholder(new LevelCurrent("%armor_light_level%", SkillType.ARMOR_LIGHT));
        registerPlaceholder(new LevelCurrent("%enchanting_level%", SkillType.ENCHANTING));
        registerPlaceholder(new LevelCurrent("%farming_level%", SkillType.FARMING));
        registerPlaceholder(new LevelCurrent("%mining_level%", SkillType.MINING));
        registerPlaceholder(new LevelCurrent("%trading_level%", SkillType.TRADING));
        registerPlaceholder(new LevelCurrent("%unarmed_level%", SkillType.UNARMED));
        registerPlaceholder(new LevelCurrent("%weapons_heavy_level%", SkillType.WEAPONS_HEAVY));
        registerPlaceholder(new LevelCurrent("%weapons_light_level%", SkillType.WEAPONS_LIGHT));
        registerPlaceholder(new LevelCurrent("%woodcutting_level%", SkillType.WOODCUTTING));

        registerPlaceholder(new LevelNext("%account_level_next%", SkillType.ACCOUNT));
        registerPlaceholder(new LevelNext("%smithing_level_next%", SkillType.SMITHING));
        registerPlaceholder(new LevelNext("%acrobatics_level_next%", SkillType.ACROBATICS));
        registerPlaceholder(new LevelNext("%alchemy_level_next%", SkillType.ALCHEMY));
        registerPlaceholder(new LevelNext("%archery_level_next%", SkillType.ARCHERY));
        registerPlaceholder(new LevelNext("%armor_heavy_level_next%", SkillType.ARMOR_HEAVY));
        registerPlaceholder(new LevelNext("%armor_light_level_next%", SkillType.ARMOR_LIGHT));
        registerPlaceholder(new LevelNext("%enchanting_level_next%", SkillType.ENCHANTING));
        registerPlaceholder(new LevelNext("%farming_level_next%", SkillType.FARMING));
        registerPlaceholder(new LevelNext("%mining_level_next%", SkillType.MINING));
        registerPlaceholder(new LevelNext("%trading_level_next%", SkillType.TRADING));
        registerPlaceholder(new LevelNext("%unarmed_level_next%", SkillType.UNARMED));
        registerPlaceholder(new LevelNext("%weapons_heavy_level_next%", SkillType.WEAPONS_HEAVY));
        registerPlaceholder(new LevelNext("%weapons_light_level_next%", SkillType.WEAPONS_LIGHT));
        registerPlaceholder(new LevelNext("%woodcutting_level_next%", SkillType.WOODCUTTING));

        registerPlaceholder(new GeneralPlayerName("%player_name%"));

        registerPlaceholder(new GeneralPlayerNickName("%player_nickname%"));

        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_general%", null));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_wood%", MaterialClass.WOOD));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_leather%", MaterialClass.LEATHER));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_stone%", MaterialClass.STONE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_chain%", MaterialClass.CHAINMAIL));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_iron%", MaterialClass.IRON));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_gold%", MaterialClass.GOLD));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_diamond%", MaterialClass.DIAMOND));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_netherite%", MaterialClass.NETHERITE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_prismarine%", MaterialClass.PRISMARINE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_membrane%", MaterialClass.MEMBRANE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_bow%", MaterialClass.BOW));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_crossbow%", MaterialClass.CROSSBOW));

        registerPlaceholder(new SmithingSkill("%skill_general%", null));
        registerPlaceholder(new SmithingSkill("%skill_wood%", MaterialClass.WOOD));
        registerPlaceholder(new SmithingSkill("%skill_leather%", MaterialClass.LEATHER));
        registerPlaceholder(new SmithingSkill("%skill_stone%", MaterialClass.STONE));
        registerPlaceholder(new SmithingSkill("%skill_chain%", MaterialClass.CHAINMAIL));
        registerPlaceholder(new SmithingSkill("%skill_iron%", MaterialClass.IRON));
        registerPlaceholder(new SmithingSkill("%skill_gold%", MaterialClass.GOLD));
        registerPlaceholder(new SmithingSkill("%skill_diamond%", MaterialClass.DIAMOND));
        registerPlaceholder(new SmithingSkill("%skill_netherite%", MaterialClass.NETHERITE));
        registerPlaceholder(new SmithingSkill("%skill_prismarine%", MaterialClass.PRISMARINE));
        registerPlaceholder(new SmithingSkill("%skill_membrane%", MaterialClass.MEMBRANE));
        registerPlaceholder(new SmithingSkill("%skill_bow%", MaterialClass.BOW));
        registerPlaceholder(new SmithingSkill("%skill_crossbow%", MaterialClass.CROSSBOW));

        registerPlaceholder(new Skillpoints("%skillpoints%"));
    }

    public void registerPlaceholder(Placeholder p){
        placeholders.put(p.getPlaceholder(), p);
    }

    public Map<String, Placeholder> getPlaceholders() {
        return placeholders;
    }

    public static String parse(String stringToParse, Player p){
        for (Placeholder s : PlaceholderManager.getInstance().getPlaceholders().values()){
            if (stringToParse.contains(s.getPlaceholder())){
                stringToParse = s.parse(stringToParse, p);
            }
        }
        return stringToParse;
    }
}
