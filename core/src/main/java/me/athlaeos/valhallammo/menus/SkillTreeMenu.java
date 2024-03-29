package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Perk;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class SkillTreeMenu extends Menu{
    private final NamespacedKey buttonKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_button_id");
    private final List<ItemStack> skillIcons = new ArrayList<>();
    private Skill selectedSkill;
    private final Map<String, ItemStack[][]> skillTrees = new HashMap<>();
    private int currentCenterX;
    private int currentCenterY;

    private static final int arrowN = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_n", -1);
    private static final int arrowNE = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_ne", -1);
    private static final int arrowE = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_e", -1);
    private static final int arrowSE = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_se", -1);
    private static final int arrowS = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_s", -1);
    private static final int arrowSW = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_sw", -1);
    private static final int arrowW = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_w", -1);
    private static final int arrowNW = ConfigManager.getInstance().getConfig("config.yml").get().getInt("skilltree_arrow_data_nw", -1);
    private final ItemStack directionN;
    private final ItemStack directionNE;
    private final ItemStack directionE;
    private final ItemStack directionSE;
    private final ItemStack directionS;
    private final ItemStack directionSW;
    private final ItemStack directionW;
    private final ItemStack directionNW;


    public SkillTreeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        ItemStack plainDirectionButton = Utils.createItemStack(Material.ARROW, " ", null);
        directionNW = arrowNW > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowNW) : plainDirectionButton;
        directionN = arrowN > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowN) : plainDirectionButton;
        directionNE = arrowNE > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowNE) : plainDirectionButton;
        directionE = arrowE > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowE) : plainDirectionButton;
        directionSE = arrowSE > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowSE) : plainDirectionButton;
        directionS = arrowS > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowS) : plainDirectionButton;
        directionSW = arrowSW > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowSW) : plainDirectionButton;
        directionW = arrowW > 0 ? Utils.setCustomModelData(plainDirectionButton.clone(), arrowW) : plainDirectionButton;

        Skill accountSkill = SkillProgressionManager.getInstance().getSkill("ACCOUNT");
        if (accountSkill != null){
            selectedSkill = accountSkill;
            currentCenterX = selectedSkill.getCenterX();
            currentCenterY = selectedSkill.getCenterY();
        }
        buildSkillTrees();
    }

    @Override
    public String getMenuName() {
        return Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF001" : TranslationManager.getInstance().getTranslation("skilltree"));
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getCurrentItem() != null){
            assert e.getCurrentItem().getItemMeta() != null;
            switch (e.getSlot()){
                case 0: {
                    if (currentCenterY - 1 >= 0) currentCenterY -= 1;
                    if (currentCenterX - 1 >= 0) currentCenterX -= 1;
                    setMenuItems();
                    return;
                }
                case 4: {
                    if (currentCenterY - 1 >= 0) currentCenterY -= 1;
                    setMenuItems();
                    return;
                }
                case 8: {
                    if (currentCenterY - 1 >= 0) currentCenterY -= 1;
                    if (currentCenterX + 9 < currentSkillTreeWidth()) currentCenterX += 1;
                    setMenuItems();
                    return;
                }
                case 18: {
                    if (currentCenterX - 1 >= 0) currentCenterX -= 1;
                    setMenuItems();
                    return;
                }
                case 26: {
                    if (currentCenterX + 9 < currentSkillTreeWidth()) currentCenterX += 1;
                    setMenuItems();
                    return;
                }
                case 36: {
                    if (currentCenterX - 1 >= 0) currentCenterX -= 1;
                    if (currentCenterY + 5 < currentSkillTreeHeight()) currentCenterY += 1;
                    setMenuItems();
                    return;
                }
                case 40: {
                    if (currentCenterY + 5 < currentSkillTreeHeight()) currentCenterY += 1;
                    setMenuItems();
                    return;
                }
                case 44: {
                    if (currentCenterY + 5 < currentSkillTreeHeight()) currentCenterY += 1;
                    if (currentCenterX + 9 < currentSkillTreeWidth()) currentCenterX += 1;
                    setMenuItems();
                    return;
                }
            }
            if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(buttonKey, PersistentDataType.STRING)){
                String identification = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonKey, PersistentDataType.STRING);

                try {
                    if (e.getSlot() < 45) throw new IllegalArgumentException();
                    Skill selectedSkill = SkillProgressionManager.getInstance().getSkill(identification);
                    if (selectedSkill != null) {
                        this.selectedSkill = selectedSkill;
                        this.currentCenterX = selectedSkill.getCenterX();
                        this.currentCenterY = selectedSkill.getCenterY();
                    }
                } catch (IllegalArgumentException ignored){
                    if (selectedSkill == null){
                        setMenuItems();
                        return;
                    }
                    Perk p = SkillProgressionManager.getInstance().getPerk(identification, selectedSkill.getType());
                    if (p != null){
                        if (p.canUnlock(playerMenuUtility.getOwner())){
                            Profile account = ProfileManager.getManager().getProfile(playerMenuUtility.getOwner(), "ACCOUNT");
                            if (account != null){
                                if (account instanceof AccountProfile){
                                    if (((AccountProfile) account).getSpendableSkillPoints() >= p.getCost()){
                                        ((AccountProfile) account).setSpendableSkillPoints(((AccountProfile) account).getSpendableSkillPoints() - p.getCost());
                                        ProfileManager.getManager().setProfile(playerMenuUtility.getOwner(), account, "ACCOUNT");
                                        SkillProgressionManager.getInstance().unlockPerk(playerMenuUtility.getOwner(), p);
                                    } else {
                                        playerMenuUtility.getOwner().sendMessage(Utils.chat(TranslationManager.getInstance().getTranslation("warning_not_enough_skillpoints")));
                                    }
                                }
                            }
                        }
                    }
                    skillTrees.put(selectedSkill.getType(), getSkillTree(selectedSkill));
                }
            }
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {

    }

    private int currentSkillTreeHeight(){
        return skillTrees.get(selectedSkill.getType()).length;
    }

    private int currentSkillTreeWidth(){
        return skillTrees.get(selectedSkill.getType())[0].length;
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        setScrollBar();
        if (selectedSkill != null){
            if (skillTrees.containsKey(selectedSkill.getType())){
                ItemStack[][] treeView = getSkillTreeView(skillTrees.get(selectedSkill.getType()), currentCenterX, currentCenterY);
                if (!ArrayUtils.isEmpty(treeView)){
                    int index = 0;
                    if (treeView.length >= 5){
                        for (int r = 0; r < 5; r++){
                            ItemStack[] row = treeView[r];
                            if (row.length >= 9){
                                for (int i = 0; i < 9; i++){
                                    if (row[i] != null){
                                        inventory.setItem(index, row[i]);
                                    }
                                    index++;
                                }
                            }
                        }
                    }
                }
            }
        }
        inventory.setItem(0, directionNW);
        inventory.setItem(4, directionN);
        inventory.setItem(8, directionNE);
        inventory.setItem(18, directionW);
        inventory.setItem(26, directionE);
        inventory.setItem(36, directionSW);
        inventory.setItem(40, directionS);
        inventory.setItem(44, directionSE);
    }

    private void setScrollBar(){
        List<ItemStack> icons = new ArrayList<>(skillIcons);
        int iconsSize = icons.size();
        if (iconsSize > 0){
            for (ItemStack i : skillIcons){
                if (i == null) continue;
                String storedType = getItemStoredSkillType(i);
                if (storedType != null){
                    Skill s = SkillProgressionManager.getInstance().getSkill(storedType);
                    Profile acc = ProfileManager.getManager().getProfile(playerMenuUtility.getOwner(), "ACCOUNT");
                    int spendablePoints = 0;
                    if (acc != null){
                        if (acc instanceof AccountProfile){
                            spendablePoints = ((AccountProfile) acc).getSpendableSkillPoints();
                        }
                    }
                    if (s != null) {
                        Profile p = ProfileManager.getManager().getProfile(playerMenuUtility.getOwner(), storedType);
                        if (p != null){
                            ItemMeta meta = i.getItemMeta();
                            assert meta != null;
                            List<String> lore = new ArrayList<>();
                            for (String line : TranslationManager.getInstance().getList("skilltree_icon_format")){
                                double expRequired = s.expForlevel(p.getLevel() + 1);
                                lore.add(Utils.chat(line
                                        .replace("%level_current%", "" + p.getLevel())
                                        .replace("%exp_current%", String.format("%.2f", p.getExp()))
                                        .replace("%exp_next%", (expRequired < 0) ? TranslationManager.getInstance().getTranslation("max_level") : String.format("%.2f", expRequired))
                                        .replace("%exp_total%", String.format("%.2f", p.getLifetimeEXP()))
                                        .replace("%skillpoints%", "" + spendablePoints)));
                            }
                            meta.setLore(lore);
                            i.setItemMeta(meta);
                        }
                    }
                }
            }
            for (int i = 0; i < SkillProgressionManager.getInstance().getAllSkills().size(); i++){
                for (int o = 0; o < 9; o++){
                    if (o >= skillIcons.size()) break;
                    ItemStack iconToPut = skillIcons.get(o);
                    inventory.setItem(45 + o, iconToPut);
                }
                ItemStack centerItem = inventory.getItem(49);
                if (centerItem != null){
                    assert centerItem.getItemMeta() != null;
                    String stored = getItemStoredSkillType(centerItem);
                    if (stored != null){
                        if (stored.equals(this.selectedSkill.getType())){
                            break;
                        }
                    }
                }
                Collections.rotate(skillIcons, 1);
            }
        }
    }

    private String getItemStoredSkillType(ItemStack i){
        assert i.getItemMeta() != null;
        if (i.getItemMeta().getPersistentDataContainer().has(buttonKey, PersistentDataType.STRING)){
            return i.getItemMeta().getPersistentDataContainer().get(buttonKey, PersistentDataType.STRING);
        }
        return null;
    }

    // If everything goes right, this should return a 2D array of itemstacks with a size of at least 9x5
    private ItemStack[][] getSkillTree(Skill skill){
        if (skill == null) return null;
        int minX = skill.getCenterX();
        int maxX = skill.getCenterX();
        int minY = skill.getCenterY();
        int maxY = skill.getCenterY();
        int xOffset;
        int yOffset;
        for (Perk p : skill.getPerks()){
            if (p.getX() < minX) minX = p.getX();
            else if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            else if (p.getY() > maxY) maxY = p.getY();
        }
        xOffset = -minX;
        skill.setCenterX(skill.getCenterX() + xOffset);
        maxX -= minX;
        minX = 0;
        yOffset = -minY;
        skill.setCenterY(skill.getCenterY() + yOffset);
        maxY -= minY;
        minY = 0;
        skill.lock();
        int width = (maxX - minX) + 9;
        int height = (maxY - minY) + 5;
        ItemStack[][] skillTree = new ItemStack[height][width];
        for (ItemStack[] row : skillTree){
            Arrays.fill(row, null);
        }
        for (Perk p : skill.getPerks()){
            if ((!p.isHidden()) || p.shouldBeVisible(playerMenuUtility.getOwner()) || p.hasUnlocked(playerMenuUtility.getOwner())){
                ItemStack perkIcon = Utils.createItemStack(p.getIcon(), Utils.chat(p.getDisplayName()), Utils.separateStringIntoLines(p.getDescription(), 40));

                ItemMeta perkMeta = perkIcon.getItemMeta();
                assert perkMeta != null;
                if (perkMeta.hasLore()){
                    assert perkMeta.getLore() != null;
                    List<String> iconLore = new ArrayList<>(perkMeta.getLore());
                    String requirementOne = TranslationManager.getInstance().getTranslation("perk_format_requirement_one");
                    String requirementAll = TranslationManager.getInstance().getTranslation("perk_format_requirement_all");
                    String requirement = TranslationManager.getInstance().getTranslation("perk_format_requirement");
                    String separator = TranslationManager.getInstance().getTranslation("perk_format_separator");
                    String perk_requirement_warning_levels = TranslationManager.getInstance().getTranslation("perk_requirement_warning_levels");
                    String perk_requirement_warning_perks = TranslationManager.getInstance().getTranslation("perk_requirement_warning_perks");
                    String perk_requirement_status_unlockable = TranslationManager.getInstance().getTranslation("perk_requirement_status_unlockable");
                    String perk_requirement_status_unlocked = TranslationManager.getInstance().getTranslation("perk_requirement_status_unlocked");
                    String perk_requirement_warning_cost = TranslationManager.getInstance().getTranslation("perk_requirement_warning_cost");
                    Profile profile = ProfileManager.getManager().getProfile(playerMenuUtility.getOwner(), skill.getType());
                    Profile tempProfile = ProfileManager.getManager().getProfile(playerMenuUtility.getOwner(), "ACCOUNT");
                    AccountProfile account = null;
                    if (tempProfile != null){
                        if (tempProfile instanceof AccountProfile){
                            account = (AccountProfile) tempProfile;
                        }
                    }
                    for (String l : TranslationManager.getInstance().getList("skilltree_perk_format")){
                        if (l.contains("%requirements_one%")){
                            if (p.getRequirement_perk_one().size() > 0){
                                if (requirementOne != null){
                                    if (!requirementOne.equals("")){
                                        iconLore.add(Utils.chat(requirementOne));
                                    }
                                }

                                if (requirement != null){
                                    if (!requirement.equals("")){
                                        for (String stringPerk : p.getRequirement_perk_one()){
                                            Perk perk = SkillProgressionManager.getInstance().getPerk(stringPerk, skill.getType());
                                            if (perk != null){
                                                iconLore.add(Utils.chat(requirement.replace("%perk_required%", perk.getDisplayName())));
                                            }
                                        }
                                        if (separator != null){
                                            if (!separator.equals(" ")){
                                                iconLore.add(Utils.chat(separator));
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (l.contains("%requirements_all%")){
                            if (p.getRequirement_perk_all().size() > 0){
                                if (requirementAll != null){
                                    if (!requirementAll.equals("")){
                                        iconLore.add(Utils.chat(requirementAll));
                                    }
                                }

                                if (requirement != null){
                                    if (!requirement.equals("")){
                                        for (String stringPerk : p.getRequirement_perk_all()){
                                            Perk perk = SkillProgressionManager.getInstance().getPerk(stringPerk, skill.getType());
                                            if (perk != null){
                                                iconLore.add(Utils.chat(requirement.replace("%perk_required%", perk.getDisplayName())));
                                            }
                                        }
                                        if (separator != null){
                                            if (!separator.equals(" ")){
                                                iconLore.add(Utils.chat(separator));
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (profile != null && l.contains("%warning_levels%")){
                            if (!p.metLevelRequirement(playerMenuUtility.getOwner())){
                                if (perk_requirement_warning_levels != null){
                                    if (!perk_requirement_warning_levels.equals("")){
                                        iconLore.add(Utils.chat(perk_requirement_warning_levels));
                                    }
                                }
                            }
                        } else if (profile != null && l.contains("%warning_perks%")){
                            if (account != null && !p.metAllPerkRequirement(account) && !p.metSinglePerkRequirement(account)){
                                if (perk_requirement_warning_perks != null){
                                    if (!perk_requirement_warning_perks.equals("")){
                                        iconLore.add(Utils.chat(perk_requirement_warning_perks));
                                    }
                                }
                            }
                        } else if (profile != null && l.contains("%warning_cost%")){
                            if (account != null && p.getCost() > account.getSpendableSkillPoints()){
                                if (perk_requirement_warning_cost != null){
                                    if (!perk_requirement_warning_cost.equals("")){
                                        iconLore.add(Utils.chat(perk_requirement_warning_cost));
                                    }
                                }
                            }
                        } else if (profile != null && l.contains("%status_unlocked%")){
                            if (account != null && p.hasUnlocked(playerMenuUtility.getOwner())){
                                if (perk_requirement_status_unlocked != null){
                                    if (!perk_requirement_status_unlocked.equals("")){
                                        iconLore.add(Utils.chat(perk_requirement_status_unlocked));
                                    }
                                }
                            }
                        } else if (profile != null && l.contains("%status_unlockable%")){
                            if (p.canUnlock(playerMenuUtility.getOwner())){
                                if (perk_requirement_status_unlockable != null){
                                    if (!perk_requirement_status_unlockable.equals("")){
                                        iconLore.add(Utils.chat(perk_requirement_status_unlockable));
                                    }
                                }
                            }
                        } else {
                            iconLore.add(Utils.chat(l
                                    .replace("%level_required%", "" + p.getRequirement_level())
                                    .replace("%skillpoint_cost%", "" + p.getCost()))
                            );
                        }
                    }
                    perkMeta.setLore(iconLore);
                }
                if (p.hasUnlocked(playerMenuUtility.getOwner())){
                    if (p.getCustomModelDataUnlocked() > 0){
                        perkMeta.setCustomModelData(p.getCustomModelDataUnlocked());
                    }
                } else if (p.shouldBeVisible(playerMenuUtility.getOwner())){
                    perkMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    if (p.getCustomModelDataUnlockable() > 0){
                        perkMeta.setCustomModelData(p.getCustomModelDataUnlockable());
                    }
                } else {
                    if (p.getCustomModelDataVisible() > 0){
                        perkMeta.setCustomModelData(p.getCustomModelDataVisible());
                    }
                }
                perkMeta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, p.getName());
                perkMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DYE);
                perkIcon.setItemMeta(perkMeta);
                skillTree[p.getY() + 2 + yOffset][p.getX() + 4 + xOffset] = perkIcon;
            }
        }
        return skillTree;
    }

    // If everything goes right, this should return a 9x5 section 2D array of itemstacks given a center point x and y of
    // the whole skill tree map. If the given map isn't at least 9x5 in size, it returns an empty 9x5 array.
    private ItemStack[][] getSkillTreeView(ItemStack[][] fullSkillTree, int centerX, int centerY){
        ItemStack[][] view = new ItemStack[5][9];
        centerX += 4;
        centerY += 2;
        if (centerY - 2 < 0) {
            centerY = 2;
        }
        if (centerY + 2 >= fullSkillTree.length) {
            centerY = fullSkillTree.length - 1;
        }
        if (fullSkillTree.length < 5){
            return view;
        }
        ItemStack[][] skillTreeYSection = Arrays.copyOfRange(fullSkillTree, centerY - 2, centerY + 3);
        for (int i = 0; i < skillTreeYSection.length; i++){
            ItemStack[] row = skillTreeYSection[i];
            if (row != null){
                if (row.length < 9) {
                    return view;
                }
                if (centerX - 4 < 0) {
                    centerX = 4;
                }
                if (centerX + 4 >= row.length) {
                    centerX = row.length - 1;
                }
                ItemStack[] nineWideRow = Arrays.copyOfRange(row, centerX - 4, centerX + 5);
                view[i] = nineWideRow;
            }
        }
        return view;
    }

    private void buildSkillTrees(){
        ValhallaMMO.getPlugin().getServer().getScheduler().runTaskAsynchronously(ValhallaMMO.getPlugin(), () -> {
            CooldownManager.getInstance().startTimerNanos(playerMenuUtility.getOwner().getUniqueId(), "benchmark_lag");
            List<Skill> skills = new ArrayList<>(SkillProgressionManager.getInstance().getAllSkills().values());
            skills.sort(Comparator.comparingInt(Skill::getSkillTreeMenuOrderPriority));
            for (Skill s : skills){
                if (!s.isActive()) continue;
                ItemStack skillIcon = Utils.createItemStack(s.getIcon(),
                        Utils.chat(s.getDisplayName()),
                        Utils.separateStringIntoLines(Utils.chat(s.getDescription()), 40));

                ItemMeta iconMeta = skillIcon.getItemMeta();
                assert iconMeta != null;
                iconMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DYE);
                iconMeta.getPersistentDataContainer().set(buttonKey, PersistentDataType.STRING, s.getType());
                if (s.getIconCustomModelData() > 0){
                    iconMeta.setCustomModelData(s.getIconCustomModelData());
                }
                skillIcon.setItemMeta(iconMeta);

                skillIcons.add(skillIcon);
                skillTrees.put(s.getType(), getSkillTree(s));
            }
            // makes sure there are enough items in the skillIcons to fill a 9-item row of icons
            for (int i = 0; i < 9; i++){
                if (skillIcons.size() >= 9) break;
                skillIcons.addAll(new ArrayList<>(skillIcons));
            }
            setMenuItems();
        });
    }
}
