package azaka7.armorauras.main;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import azaka7.armorauras.client.ClientHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAuraCharm extends Item{
	
	public ItemAuraCharm(){
		this.setMaxDamage(0);
	    this.setHasSubtypes(true);
	    this.setMaxStackSize(1);
	    this.setCreativeTab(CreativeTabs.TOOLS);
	}
	
	@Override
	public int getMetadata(int damage) {
	  return damage;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
		return this.getUnlocalizedName()+"."+getAuraType(stack).toString().toLowerCase();
    }
	
	@Override
	public int getMetadata(ItemStack stack)
    {
        return stack.getItemDamage() >= EnumAuraType.values().length ? 0 : stack.getItemDamage();
    }
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		if(stack.getItem() == this){
			ClientHandler client = ClientHandler.instance();
			tooltip.add("\u00a76"+EnumAuraType.values()[stack.getMetadata()].getName()+" "+client.localize("a7armorauras.tooltip.aura"));
			tooltip.add("\u00a77 "+client.localize("a7armorauras.tooltip.charm.activate"));
			tooltip.add("\u00a77 "+client.localize("a7armorauras.tooltip.charm.armor"));
		}
    }
	
	@SideOnly(Side.CLIENT)
	  @Override
	  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	  {
		if(this.isInCreativeTab(tab)){
			for (EnumAuraType aura : EnumAuraType.values()) {
			      ItemStack subItemStack = new ItemStack(this, 1, aura.getMetadata());
			      subItems.add(subItemStack);
			    }
		}
	  }
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entityIn, int slot, boolean isSelected)
    {
		if(!(entityIn instanceof EntityLivingBase)){return;}
		EntityLivingBase entity = (EntityLivingBase) entityIn;
		
		if(isSelected || stack.equals(entity.getHeldItemOffhand())){
			PotionEffect pot = ((ItemAuraCharm) stack.getItem()).getAuraType(stack).getEffect();
			if(pot == null){return;}
			byte radius = ArmorAuras.getAuraRadius();
			List<EntityLivingBase> near = world.getEntities(EntityLivingBase.class, EntitySelectors.withinRange(entity.posX, entity.posY+1, entity.posZ, radius));
			for(EntityLivingBase target : near){
				if(target != null && !target.equals(entity)){
					if(!target.isPotionActive(pot.getPotion())){
						target.addPotionEffect(new PotionEffect(pot));
					}
				}
			}
			Color color = new Color(pot.getPotion().getLiquidColor());
			if(pot.getPotion().equals(MobEffects.WITHER)){color = new Color(0xF2F0F2);}
			if(Math.random() < ArmorAuras.getParticleFactor() * 4.0D){
				double theta = 2*Math.PI;
				theta *= ((double) world.getWorldTime() % 500) / 500.0D;
				theta += 2*Math.PI*(double) ((ItemAuraCharm) stack.getItem()).getAuraType(stack).getPos() / 25.0D;
				for(int j = 0; j < 5; j++){
					double t = theta + 2*Math.PI*j/5.0D;
					world.spawnParticle(EnumParticleTypes.SPELL_MOB, entity.posX + radius*Math.cos(t), entity.posY+0.2, entity.posZ + radius*Math.sin(t), color.getRed(), color.getBlue(), color.getGreen());
				}
			}
		}
    }
	
	public EnumAuraType getAuraType(ItemStack stack){
		if(stack == null || stack.getItem() != this 
				|| stack.getMetadata() >= EnumAuraType.values().length){return EnumAuraType.NULL;}
		
		return EnumAuraType.values()[stack.getMetadata()];
	}
	
	public static enum EnumAuraType implements IStringSerializable{
		NULL(0,0,"null", null),
		SLOW(1,0,"slow", new PotionEffect(MobEffects.SLOWNESS, 60, 0, false, false)),
		SLOW2(2,0,"slow2", new PotionEffect(MobEffects.SLOWNESS, 40, 1, false, false)),
		SLOW3(3,0,"slow3", new PotionEffect(MobEffects.SLOWNESS, 20, 2, false, false)),
		WEAK(4,1,"weak", new PotionEffect(MobEffects.WEAKNESS, 60, 0, false, false)),
		WEAK2(5,1,"weak2", new PotionEffect(MobEffects.WEAKNESS, 40, 1, false, false)),
		WEAK3(6,1,"weak3", new PotionEffect(MobEffects.WEAKNESS, 20, 2, false, false)),
		POISON(7,2,"poison", new PotionEffect(MobEffects.POISON, 80, 0, false, false)),
		POISON2(8,2,"poison2", new PotionEffect(MobEffects.POISON, 60, 1, false, false)),
		WITHER(9,3,"wither", new PotionEffect(MobEffects.WITHER, 60, 0, false, false)),
		WITHER2(10,3,"wither2", new PotionEffect(MobEffects.WITHER, 60, 1, false, false)),
		NAUSEA(11,4,"nausea", new PotionEffect(MobEffects.NAUSEA, 200, 0, false, false));
		
    	//PotionEffect effect = new PotionEffect(potion, duration, amplifier, isFromBeacon, canSeeParticles);
		
		private final int meta;
		private final int pos;
	    private final String name;
	    private final PotionEffect effect;
	    
	    private EnumAuraType(int meta, int pos, String name, PotionEffect effect){
	    	this.meta = meta;
	    	this.pos = pos;
	    	this.name = name;
	    	this.effect = effect;
	    }

		public int getMetadata() {
			return meta;
		}

		public String getName() {
			String unlocalized = "a7armorauras.auras."+name;
			if(this == SLOW || this == SLOW2 || this == SLOW3){
				if(ArmorAuras.isFat()){
					unlocalized = unlocalized + ".f";
				}
			}
			return ClientHandler.instance().localize(unlocalized);
		}
		
		public String getUnlocalizedName(){
			return name;
		}
		
		public PotionEffect getEffect(){
			if(this.effect == null){return null;}
			return new PotionEffect(this.effect);
		}

		public double getPos() {
			return pos;
		}
		
	}
	
}
