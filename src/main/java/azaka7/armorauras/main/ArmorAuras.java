package azaka7.armorauras.main;

import java.io.File;
import java.io.IOException;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Logger;

@Mod(modid = ArmorAuras.MODID, name = ArmorAuras.NAME, version = ArmorAuras.VERSION)
public class ArmorAuras
{
    public static final String MODID = "a7armorauras";
    public static final String NAME = "A7 Armor Auras";
    public static final String VERSION = "1.2b";

    private static boolean isClient = false;;
    private static Logger logger;
    private static Configuration config;
    
    private static float particleFactor;
    public static float getParticleFactor(){return particleFactor;}
    
    private static byte auraRadius;
    public static byte getAuraRadius(){return auraRadius;}
    
    private static int xpCost;
    public static int getXPCost(){return xpCost;}
    
    private static int helmCount = 1;
    private static int chestCount = 3;
    private static int legCount = 2;
    private static int bootCount = 0;
    public static int[] getArmorLimits(){return new int[]{bootCount,legCount,chestCount,helmCount};}
    
    private static boolean holderToggle;
    private static boolean[] auraToggles;
    private static boolean[] selfAuraToggles;
    private static boolean fattyMode;
    public static boolean isFat(){return fattyMode;}

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        String cfg = event.getModConfigurationDirectory().getAbsolutePath()+"/"+MODID+".cfg";
        File file = new File(cfg);
        if(!file.exists()){
        	try {
				file.createNewFile();
			} catch (IOException e) {}
        }
        config = new Configuration(new File(cfg));
        
        config.setCategoryComment("Graphics", "Manage graphic settings of auras");
        particleFactor = config.getFloat("Particle Factor", "Graphics", 0.15f, 0.0f, 1.0f, "Controls the rate of aura particle spawning (0.00-1.00 -> 0%-100%) Default:0.15");
        
        config.setCategoryComment("General", "Manage general mod settings");
        auraRadius = (byte) config.getInt("Aura Radius", "General",5, 0, 127, "Radius of outward aura effects (up to 127)");
        xpCost = config.getInt("Armor Charm XP Cost","General",30,0,Short.MAX_VALUE,"The amount of XP required to apply an aura charm to a piece of armor.");
        fattyMode = config.getBoolean("Fatty Mode", "General", false, "Replaces slowness aura translation with a less politically correct term");
        
        config.setCategoryComment("Armor Settings", "Determine the number of auras each armor type can handle");
        helmCount = config.getInt("Helmet", "Armor Settings", 1, 0, 127, "Default: 1");
        chestCount = config.getInt("Chestplate", "Armor Settings", 3, 0, 127, "Default: 3");
        legCount = config.getInt("Leggings", "Armor Settings", 2, 0, 127, "Default: 2");
        bootCount = config.getInt("Boots", "Armor Settings", 0, 0, 127, "Default: 0");
        
        auraToggles = new boolean[ItemAuraCharm.EnumAuraType.values().length];
        selfAuraToggles = new boolean[ItemSelfAuraCharm.EnumSelfAuraType.values().length];
        config.setCategoryComment("Auras", "Toggle the provided crafting of auras. Set to false to disable the crafting recipe provided by this mod. Useful for maps and modpacks.");
        holderToggle = config.getBoolean("Aura Activator", "Auras", true, "");
        auraToggles[0] = config.getBoolean("Aura Charm", "Auras", true, "");
        for(int i = 1; i < auraToggles.length; i++){
            auraToggles[i] = config.getBoolean("Aura Charm ("+ItemAuraCharm.EnumAuraType.values()[i].getUnlocalizedName()+")", "Auras", true, "");
        }
        selfAuraToggles[0] = config.getBoolean("Inner Aura Charm", "Auras", true, "");
        for(int i = 1; i < selfAuraToggles.length; i++){
            selfAuraToggles[i] = config.getBoolean("Inner Aura Charm ("+ItemSelfAuraCharm.EnumSelfAuraType.values()[i].getUnlocalizedName()+")", "Auras", true, "");
        }
        
        config.save();
       
       if(event.getSide() == Side.CLIENT){
    	   isClient = true;
    	   azaka7.armorauras.client.ClientHandler.instance();
       }
       
       AAItems.registerModItems();
       
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	final Item aura_charm = AAItems.getItem("aura_charm");
    	final Item self_charm = AAItems.getItem("self_charm");
    	final Item holder = AAItems.getItem("holder");
    	ResourceLocation group = new ResourceLocation(MODID+":aura_charms");
    	
    	if(holderToggle)
        	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":holder"), new ResourceLocation(MODID+":aura_holder"), 
        			new ItemStack(holder, 1, 0),
        			"cec",
        			"ebe",
        			"cec",
        			'b', Blocks.BEACON,
        			'c', Items.END_CRYSTAL,
        			'e', Blocks.EMERALD_BLOCK
        	);
    	if(auraToggles[0])
    	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":aura_charm_0"), group, 
    			new ItemStack(aura_charm, 1, 0), 
    			"ege",
    			"gcg",
    			"ege",
    			'e', Items.ENDER_EYE,
    			'g', Blocks.GOLD_BLOCK,
    			'c', Items.END_CRYSTAL
    	);
    	//SLOWNESS
    	if(auraToggles[1])
    	GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":aura_charm_1"), group,
    			new ItemStack(aura_charm, 1, 1),
    			Ingredient.fromStacks(new ItemStack(aura_charm, 1, 0)),
    			Ingredient.fromItem(Items.NETHER_STAR),
    			Ingredient.fromItem(Items.NETHER_WART),
    			Ingredient.fromItem(Items.SUGAR),
    			Ingredient.fromItem(Items.FERMENTED_SPIDER_EYE),
    			Ingredient.fromItem(Items.DIAMOND)
    	);
    	if(auraToggles[2])
    	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":aura_charm_2"), group, 
    			new ItemStack(aura_charm, 1, 2), 
    			" c ",
    			"cxc",
    			" c ",
    			'c', new ItemStack(aura_charm, 1, 1),
    			'x', Items.EMERALD
    	);
    	if(auraToggles[3])
    	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":aura_charm_3"), group, 
    			new ItemStack(aura_charm, 1, 3), 
    			" c ",
    			"cxc",
    			" c ",
    			'c', new ItemStack(aura_charm, 1, 2),
    			'x', Items.EMERALD
    	);
    	//WEAKNESS
    	if(auraToggles[4])
    	GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":aura_charm_4"), group,
    			new ItemStack(aura_charm, 1, 4),
    			Ingredient.fromStacks(new ItemStack(aura_charm, 1, 0)),
    			Ingredient.fromItem(Items.NETHER_STAR),
    			Ingredient.fromItem(Items.NETHER_WART),
    			Ingredient.fromItem(Items.FERMENTED_SPIDER_EYE),
    			Ingredient.fromItem(Items.REDSTONE),
    			Ingredient.fromItem(Items.DIAMOND)
    	);
    	if(auraToggles[5])
    	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":aura_charm_5"), group, 
    			new ItemStack(aura_charm, 1, 5), 
    			" c ",
    			"cxc",
    			" c ",
    			'c', new ItemStack(aura_charm, 1, 4),
    			'x', Items.EMERALD
    	);
    	if(auraToggles[6])
    	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":aura_charm_6"), group, 
    			new ItemStack(aura_charm, 1, 6), 
    			" c ",
    			"cxc",
    			" c ",
    			'c', new ItemStack(aura_charm, 1, 5),
    			'x', Items.EMERALD
    	);
    	//POISON
    	if(auraToggles[7])
    	GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":aura_charm_7"), group,
    			new ItemStack(aura_charm, 1, 7),
    			Ingredient.fromStacks(new ItemStack(aura_charm, 1, 0)),
    			Ingredient.fromItem(Items.NETHER_STAR),
    			Ingredient.fromItem(Items.NETHER_WART),
    			Ingredient.fromItem(Items.SPIDER_EYE),
    			Ingredient.fromItem(Items.GLOWSTONE_DUST),
    			Ingredient.fromItem(Items.DIAMOND)
    	);
    	if(auraToggles[8])
    	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":aura_charm_8"), group, 
    			new ItemStack(aura_charm, 1, 8), 
    			" c ",
    			"cxc",
    			" c ",
    			'c', new ItemStack(aura_charm, 1, 7),
    			'x', Items.EMERALD
    	);
    	//WITHER
    	if(auraToggles[9])
    	GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":aura_charm_9"), group,
    			new ItemStack(aura_charm, 1, 9),
    			Ingredient.fromStacks(new ItemStack(aura_charm, 1, 0)),
    			Ingredient.fromItem(Items.NETHER_STAR),
    			Ingredient.fromItem(Items.NETHER_WART),
    			Ingredient.fromStacks(new ItemStack(Items.SKULL, 1, 1)),
    			Ingredient.fromItem(Items.FIRE_CHARGE),
    			Ingredient.fromItem(Items.DIAMOND)
    	);
    	if(auraToggles[10])
    	GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":aura_charm_10"), group, 
    			new ItemStack(aura_charm, 1, 10), 
    			" c ",
    			"cxc",
    			" c ",
    			'c', new ItemStack(aura_charm, 1, 9),
    			'x', Items.EMERALD
    	);
    	//NAUSEA
    	if(auraToggles[11])
    	GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":aura_charm_11"), group,
    			new ItemStack(aura_charm, 1, 11),
    			Ingredient.fromStacks(new ItemStack(aura_charm, 1, 0)),
    			Ingredient.fromItem(Items.NETHER_STAR),
    			Ingredient.fromItem(Items.NETHER_WART),
    			Ingredient.fromStacks(new ItemStack(Items.FISH, 1, 3)),//puffer fish
    			Ingredient.fromItem(Items.ROTTEN_FLESH),
    			Ingredient.fromItem(Items.DIAMOND)
    	);
    	
    	group = new ResourceLocation(MODID+":self_charms");
    	
    	//INNER AURA CHARM
    	if(selfAuraToggles[0])
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":self_charm_0"), group, 
        			new ItemStack(self_charm, 1, 0), 
        			"ede",
        			"dcd",
        			"ede",
        			'e', Items.EMERALD,
        			'd', Blocks.DIAMOND_BLOCK,
        			'c', new ItemStack(aura_charm, 1, 0)
        );
    	
    	//SPEED
    	if(selfAuraToggles[1])
        GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":self_charm_1"), group, 
        			new ItemStack(self_charm, 1, 1),
        			Ingredient.fromStacks(new ItemStack(self_charm, 1, 0)),
        			Ingredient.fromItem(Items.NETHER_STAR), 
        			Ingredient.fromItem(Items.NETHER_WART), 
        			Ingredient.fromItem(Items.SUGAR), 
        			Ingredient.fromItem(Items.REDSTONE), 
        			Ingredient.fromItem(Items.EMERALD)
        );
    	if(selfAuraToggles[2])
        GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":self_charm_2"), group, 
            		new ItemStack(self_charm, 1, 2), 
            		" c ",
            		"cpc",
            		" c ",
            		'p', Items.ENDER_PEARL,
            		'c', new ItemStack(self_charm, 1, 1)
        );
    	if(selfAuraToggles[3])
            GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":self_charm_3"), group, 
                		new ItemStack(self_charm, 1, 3), 
                		" c ",
                		"cpc",
                		" c ",
                		'p', Items.ENDER_PEARL,
                		'c', new ItemStack(self_charm, 1, 2)
            );
    	//STRENGTH
    	if(selfAuraToggles[4])
            GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":self_charm_4"), group, 
            			new ItemStack(self_charm, 1, 4),
            			Ingredient.fromStacks(new ItemStack(self_charm, 1, 0)),
            			Ingredient.fromItem(Items.NETHER_STAR), 
            			Ingredient.fromItem(Items.NETHER_WART), 
            			Ingredient.fromItem(Items.BLAZE_POWDER), 
            			Ingredient.fromItem(Items.GLOWSTONE_DUST), 
            			Ingredient.fromItem(Items.EMERALD)
            );
        if(selfAuraToggles[5])
            GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":self_charm_5"), group, 
                		new ItemStack(self_charm, 1, 5), 
                		" c ",
                		"cpc",
                		" c ",
                		'p', Items.ENDER_PEARL,
                		'c', new ItemStack(self_charm, 1, 4)
            );
        //RESISTANCE
        if(selfAuraToggles[6])
            GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":self_charm_6"), group, 
            		new ItemStack(self_charm, 1, 6),
            		Ingredient.fromStacks(new ItemStack(self_charm, 1, 0)),
            		Ingredient.fromItem(Items.NETHER_STAR), 
            		Ingredient.fromItem(Items.NETHER_WART), 
            		Ingredient.fromItem(Items.QUARTZ), 
            		Ingredient.fromItem(Items.SHULKER_SHELL), 
            		Ingredient.fromItem(Items.EMERALD)
            );
        if(selfAuraToggles[7])
            GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":self_charm_7"), group, 
                	new ItemStack(self_charm, 1, 7), 
                	" c ",
                	"cpc",
                	" c ",
                	'p', Items.ENDER_PEARL,
                	'c', new ItemStack(self_charm, 1, 6)
            );
        if(selfAuraToggles[8])
            GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":self_charm_8"), group, 
                    new ItemStack(self_charm, 1, 8), 
                    " c ",
                    "cpc",
                    " c ",
                    'p', Items.ENDER_PEARL,
                    'c', new ItemStack(self_charm, 1, 7)
            );
        //REGEN
    	if(selfAuraToggles[9])
            GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":self_charm_9"), group, 
            		new ItemStack(self_charm, 1, 9),
            		Ingredient.fromStacks(new ItemStack(self_charm, 1, 0)),
            		Ingredient.fromItem(Items.NETHER_STAR), 
            		Ingredient.fromItem(Items.NETHER_WART), 
            		Ingredient.fromItem(Items.GHAST_TEAR), 
            		Ingredient.fromItem(Items.GLOWSTONE_DUST), 
            		Ingredient.fromItem(Items.EMERALD)
            );
        if(selfAuraToggles[10])
            GameRegistry.addShapedRecipe(new ResourceLocation(MODID+":self_charm_10"), group, 
                	new ItemStack(self_charm, 1, 10), 
                	" c ",
                	"cpc",
                	" c ",
                	'p', Items.ENDER_PEARL,
                	'c', new ItemStack(self_charm, 1, 9)
            );
        //HASTE
    	if(selfAuraToggles[11])
            GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":self_charm_11"), group, 
            		new ItemStack(self_charm, 1, 11),
            		Ingredient.fromStacks(new ItemStack(self_charm, 1, 0)),
            		Ingredient.fromItem(Items.NETHER_STAR), 
            		Ingredient.fromItem(Items.NETHER_WART), 
            		Ingredient.fromItem(Items.DIAMOND_PICKAXE), 
            		Ingredient.fromStacks(new ItemStack(Blocks.TNT)), 
            		Ingredient.fromItem(Items.EMERALD)
            );
    	//LUCK
    	if(selfAuraToggles[12])
            GameRegistry.addShapelessRecipe(new ResourceLocation(MODID+":self_charm_12"), group, 
            		new ItemStack(self_charm, 1, 12),
            		Ingredient.fromStacks(new ItemStack(self_charm, 1, 0)),
            		Ingredient.fromItem(Items.NETHER_STAR), 
            		Ingredient.fromItem(Items.NETHER_WART), 
            		Ingredient.fromItem(Items.RABBIT_FOOT), 
            		Ingredient.fromStacks(new ItemStack(Items.DYE, 1, 4)), 
            		Ingredient.fromItem(Items.EMERALD)
            );
    }

	public static boolean isClient() {
		return isClient;
	}
	
	/*	//For use if potions should be involved in crafting
    public static void addNBTShapedRecipe(ResourceLocation name, ResourceLocation group, @Nonnull ItemStack output, Object... params)
    {
        ShapedPrimer primer = CraftingHelper.parseShaped(params);
        for(int i = 0; i < primer.input.size(); i++){
        	Ingredient ing = primer.input.get(i);
        	Ingredient newIng = new IngredientNBT(ing);
        	primer.input.set(i, newIng);
        }
        GameData.register_impl(new ShapedRecipes(group == null ? "" : group.toString(), primer.width, primer.height, primer.input, output).setRegistryName(name));
    }
    
    public static void addNBTShapelessRecipe(ResourceLocation name, ResourceLocation group, @Nonnull ItemStack output, Object... params)
    {
        ShapedPrimer primer = CraftingHelper.parseShaped(params);
        for(int i = 0; i < primer.input.size(); i++){
        	Ingredient ing = primer.input.get(i);
        	if(ing.getMatchingStacks()[0].getItem() instanceof ItemPotion){
        		Ingredient newIng = new IngredientNBT(ing);
            	primer.input.set(i, newIng);
        	}
        }
        GameData.register_impl(new ShapedRecipes(group == null ? "" : group.toString(), primer.width, primer.height, primer.input, output).setRegistryName(name));
    }
    
    private static class IngredientNBT extends Ingredient {
    	//private final Ingredient parent;
    	
    	public IngredientNBT(ItemStack... stacks){
    		super(stacks);
    	}
    	
    	public IngredientNBT(Ingredient parent){
    		super(parent.getMatchingStacks());
    		//this.parent = parent;
    	}
    	
    	@Override
    	public boolean apply(@Nullable ItemStack applied)
        {
            if (applied == null)
            {
                return false;
            }
            else
            {
                for (ItemStack itemstack : this.getMatchingStacks())
                {
                	if(ItemStack.areItemStacksEqual(itemstack, applied) || ItemStack.areItemStacksEqualUsingNBTShareTag(itemstack, applied)){
                		return true;
                	}
                }

                return false;
            }
        }
    }*/
}
