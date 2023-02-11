package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.PartySpyCommand;
import me.athlaeos.valhallammo.dom.Party;
import me.athlaeos.valhallammo.managers.PartyManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
//        if (e.getMessage().equals("bleedme")){
//            // this is a test "command" to inflict bleeding damage on the sender, "inflicted" by a random entity
//            new BukkitRunnable(){
//                @Override
//                public void run() {
//                    List<LivingEntity> entities = ValhallaMMO.getPlugin().getServer().getWorlds().get(0).getLivingEntities();
//                    PotionEffectManager.getInstance().bleedEntity(e.getPlayer(),
//                            entities.get(Utils.getRandom().nextInt(entities.size())), 15000, 4);
//                }
//            }.runTaskLater(ValhallaMMO.getPlugin(), 1L);
//        }

//        if (e.getMessage().contains("do it")){
//            ItemDictionaryManager.getInstance().getItemDictionary().put(0, new ItemStack(Material.DIAMOND));
//            ItemDictionaryManager.getInstance().getItemDictionary().put(1, new ItemStack(Material.IRON_INGOT));
//            ItemDictionaryManager.getInstance().getItemDictionary().put(2, new ItemStack(Material.GOLD_INGOT));
//            ItemDictionaryManager.getInstance().getItemDictionary().put(3, new ItemStack(Material.LEATHER));
//            ItemDictionaryManager.getInstance().getItemDictionary().put(4, new ItemStack(Material.IRON_NUGGET));
//            ItemDictionaryManager.getInstance().getItemDictionary().put(5, new ItemStack(Material.OAK_PLANKS));
//            ItemDictionaryManager.shouldSaveItems();
//
//            for (DynamicCraftingTableRecipe recipe : CustomRecipeManager.getInstance().getShapedRecipes().values()){
//                if (recipe.getName().startsWith("salvage_")){
//                    int amountToSet = 0;
//                    int idToUse = 0;
//                    if (recipe.getName().contains("sword")){
//                        amountToSet = 2;
//                    } else if (recipe.getName().contains("pickaxe")){
//                        amountToSet = 3;
//                    } else if (recipe.getName().contains("axe")){
//                        amountToSet = 3;
//                    } else if (recipe.getName().contains("hoe")){
//                        amountToSet = 2;
//                    } else if (recipe.getName().contains("helmet")){
//                        amountToSet = 5;
//                    } else if (recipe.getName().contains("chestplate")){
//                        amountToSet = recipe.getName().contains("chain") ? 24 : 8;
//                    } else if (recipe.getName().contains("leggings")){
//                        amountToSet = recipe.getName().contains("chain") ? 23 : 7;
//                    } else if (recipe.getName().contains("boots")){
//                        amountToSet = recipe.getName().contains("chain") ? 20 : 4;
//                    }
//                    if (recipe.getName().contains("iron")){
//                        idToUse = 1;
//                    } else if (recipe.getName().contains("gold")){
//                        idToUse = 2;
//                    } else if (recipe.getName().contains("leather")){
//                        idToUse = 3;
//                    } else if (recipe.getName().contains("chain")){
//                        idToUse = 4;
//                    } else if (recipe.getName().contains("wood")){
//                        idToUse = 5;
//                    }
//                    recipe.setImproveFirstEquipment(true);
//                    SetAmountModifier mod0 = new SetAmountModifier("set_amount");
//                    mod0.setPriority(ModifierPriority.SOONEST);
//                    mod0.setStrength(amountToSet);
//                    DynamicAmountModifier mod1 = new DynamicAmountModifier("dynamic_amount");
//                    mod1.setPriority(ModifierPriority.SOON);
//                    mod1.setStrength(100);
//                    mod1.setStrength2(0);
//                    mod1.setStrength3(0);
//                    ChangeItemToDictionaryItemKeepingAmountModifier mod2 = new ChangeItemToDictionaryItemKeepingAmountModifier("change_item_keeping_amount");
//                    mod2.setPriority(ModifierPriority.SOONISH);
//                    mod2.setStrength(idToUse);
//                    List<DynamicItemModifier> modifiers = new ArrayList<>();
//                    modifiers.add(mod0);
//                    modifiers.add(mod1);
//                    modifiers.add(mod2);
//                    recipe.setModifiers(modifiers);
//
//                    System.out.println(recipe.getName() + ", replacing with " + ItemDictionaryManager.getInstance().getItemDictionary().get(idToUse).getType() + "x" + amountToSet);
//                }
//            }
//            CustomRecipeManager.shouldSaveRecipes();
//            System.out.println("done :)");
//        }

        if (e.getMessage().startsWith("!")) {
            e.setMessage(e.getMessage().replaceFirst("!", ""));
            return;
        }
        Party party = PartyManager.getInstance().getParty(e.getPlayer());
        if (party != null){
            if (PartyManager.getInstance().getPlayersInPartyChat().contains(e.getPlayer().getUniqueId()) || e.getMessage().startsWith("pc:")){
                e.setMessage(e.getMessage().replaceFirst("pc:", ""));
                PartyManager.ErrorStatus status = PartyManager.getInstance().hasPermission(e.getPlayer(), "party_chat");
                if (status == null){
                    Party.PermissionGroup rank = party.getMembers().get(e.getPlayer().getUniqueId());
                    String title = party.getLeader().equals(e.getPlayer().getUniqueId()) ? PartyManager.getInstance().getLeaderTitle() : rank == null ? "" : rank.getTitle();
                    String messageFormat = Utils.chat(PartyManager.getInstance().getPartyChatFormat());
                    String spyFormat = Utils.chat(PartyManager.getInstance().getPartySpyChatFormat());
                    String newFormat = messageFormat
                            .replace("%rank%", Utils.chat(title))
                            .replace("%party%", Utils.chat(party.getDisplayName()));
                    String newSpyFormat = spyFormat
                            .replace("%rank%", Utils.chat(title))
                            .replace("%party%", Utils.chat(party.getDisplayName()));
                    e.setFormat(newFormat);

                    e.getRecipients().clear();
                    for (UUID member : party.getMembers().keySet()){
                        Player p = ValhallaMMO.getPlugin().getServer().getPlayer(member);
                        if (p != null){
                            e.getRecipients().add(p);
                        }
                    }
                    for (UUID spy : PartySpyCommand.getPartySpies()){
                        if (!party.getMembers().containsKey(spy)){
                            Player p = ValhallaMMO.getPlugin().getServer().getPlayer(spy);
                            if (p == null) {
                                PartySpyCommand.getPartySpies().remove(spy);
                                return;
                            }
                            p.sendMessage(Utils.chat(String.format(newSpyFormat, e.getPlayer().getName(), e.getMessage())));
                        }
                    }
                } else {
                    status.sendErrorMessage(e.getPlayer());
                    e.setCancelled(true);
                }
            }
        } else if (e.getMessage().startsWith("pc:")){
            e.setCancelled(true);
            PartyManager.ErrorStatus.NOT_IN_PARTY.sendErrorMessage(e.getPlayer());
        }
    }
}
