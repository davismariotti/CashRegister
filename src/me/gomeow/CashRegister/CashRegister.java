package me.gomeow.CashRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CashRegister extends JavaPlugin {
	HashMap<String, ArrayList<String>> register = new HashMap<String, ArrayList<String>>();
	
	public static String cap(String string) {
		  char[] chars = string.toLowerCase().toCharArray();
		  boolean found = false;
		  for (int i = 0; i < chars.length; i++) {
		    if (!found && Character.isLetter(chars[i])) {
		      chars[i] = Character.toUpperCase(chars[i]);
		      found = true;
		    } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='_') { // You can add other chars here
		      found = false;
		    }
		  }
		  return String.valueOf(chars);
		}
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
	}
	private String findMaterial(String input) throws IllegalArgumentException {
		try {
			int i = Integer.decode(input);
			Material material = Material.getMaterial(i);
			if(material == null) throw new IllegalArgumentException("Unknown ID!");
			else {
				String materialname = cap(material.toString());
				return materialname.replaceAll("_", "");
			}
			
		}
		catch(NumberFormatException nfe) {
			for(Material material : Material.values()) {
				if(material.name().equals(input.toUpperCase())) {
					String materialname = cap(material.toString());
					if(materialname != null) {
						return materialname.replaceAll("_", "");
					}
					else {
						throw new IllegalArgumentException("Unknown Name!");
					}
				}
				else if(material.name().replaceAll("_", "").equals(input.toUpperCase())) {
					String materialname = cap(material.toString());
					if(materialname != null) {
						return materialname.replaceAll("_", "");
					}
					else {
						throw new IllegalArgumentException("Unknown Name!");
					}
				}
			}
		}
		return input;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String name = sender.getName();
		if(label.equalsIgnoreCase("order")) {
			if(args.length == 0) {
				sender.sendMessage(ChatColor.YELLOW + "Commands:");
				sender.sendMessage(ChatColor.YELLOW + "/order add <block> <amount> - Adds an amount of an item to the total.");
				sender.sendMessage(ChatColor.YELLOW + "/order subtract <block> <amount> - Subtracts an amount of an item from the total.");
				sender.sendMessage(ChatColor.YELLOW + "/order set <block> <amount> - Sets the amount of an item in the total.");
				sender.sendMessage(ChatColor.YELLOW + "/order remove <block> - Removes an item in the total.");
				sender.sendMessage(ChatColor.YELLOW + "/order total - Shows the total.");
				sender.sendMessage(ChatColor.YELLOW + "/order finish - Clears the total.");
			}
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("add")) {
					sender.sendMessage(ChatColor.RED + "Usage: /order add <block> <amount>");
				}
				if(args[0].equalsIgnoreCase("subtract")) {
					sender.sendMessage(ChatColor.RED + "Usage: /order subtract <block> <amount>");
				}
				if(args[0].equalsIgnoreCase("set")) {
					sender.sendMessage(ChatColor.RED + "Usage: /order set <block> <amount>");
				}
				if(args[0].equalsIgnoreCase("remove")) {
					sender.sendMessage(ChatColor.RED + "Usage: /order remove <block>");
				}
				/*if(args[0].equalsIgnoreCase("helpme")) {
					
					for(Material each : Material.class.getEnumConstants()) {
						int materialid = each.getId();
						String pathmaterial = cap(each.toString());
						this.getConfig().set("Prices." + pathmaterial.replaceAll("_", ""), materialid);
						
					}
					saveConfig();
				}*/
				else if(args[0].equalsIgnoreCase("total")) {
					String listname = sender.getName();
					if(!register.containsKey(sender.getName())) {
						sender.sendMessage(ChatColor.RED + "You do not have anything in your total!");
					}
					else {
						ArrayList<String> itemsinregister = register.get(listname);
						String moneysign = this.getConfig().getString("Money-Sign");
						
						double total = 0;
						
						for(String priceadd : itemsinregister) {
							String[] eachsplit = priceadd.split("@");
							total = total + this.getConfig().getDouble("Prices." + eachsplit[0]) * Double.parseDouble(eachsplit[1]);
							
						}

						total = total * 100;
						total = Math.round(total);
						total = total / 100;
						
						sender.sendMessage(ChatColor.GOLD + "In the order is: " + moneysign + Double.toString(total));
						for(String each : itemsinregister) {
							String[] eachsplit = each.split("@");
							double splittotal = 0;
							splittotal = splittotal + this.getConfig().getDouble("Prices." + findMaterial(eachsplit[0]));
							splittotal = splittotal * Double.parseDouble(eachsplit[1]);
							splittotal = splittotal * 100;
							splittotal = Math.round(splittotal);
							splittotal = splittotal / 100;
							
							sender.sendMessage(ChatColor.GOLD + moneysign + Double.toString(splittotal) + " - " + eachsplit[1] + " " + eachsplit[0]);
						}
					}
				}
				else if(args[0].equalsIgnoreCase("finish")) {
					if(register.containsKey(sender.getName())) {
						register.remove(sender.getName());
						sender.sendMessage(ChatColor.GREEN + "Order finished!");
					}
					else {
						sender.sendMessage(ChatColor.RED + "You had no order to begin with!");
					}
				}
				else {
					sender.sendMessage(ChatColor.RED + "Please type /order to get the available commands!");
				}
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("add")) {
					sender.sendMessage(ChatColor.RED + "Usage: /order add <block> <amount>");
				}
				if(args[0].equalsIgnoreCase("subtract")) {
					sender.sendMessage(ChatColor.RED + "Usage: /order subtract <block> <amount>");
				}
				if(args[0].equalsIgnoreCase("set")) {
					sender.sendMessage(ChatColor.RED + "Usage: /order set <block> <amount>");
				}
				if(args[0].equalsIgnoreCase("remove")) {
					String block = cap(args[1]);
					if(register.containsKey(sender.getName())) {
						ArrayList<String> removelist = register.get(sender.getName());
						for(String removeitem : removelist) {
							if(removeitem.startsWith(block)) {
								removelist.remove(removeitem);
								
								register.remove(name);
								register.put(name, removelist);
								
								sender.sendMessage(ChatColor.GREEN + "Removed all " + block + " from the total!");
								break;
							}
							else if(removeitem == removelist.get(removelist.size() - 1)) {
								sender.sendMessage(ChatColor.RED + "There was no " + block + " in the total!");
							}
						}
						if(removelist.isEmpty()) {
							register.remove(name);
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You do not have anything in your total right now.");
					}
				}
				else sender.sendMessage(ChatColor.RED + "Please type /order to get the available commands!");
			}
			if(args.length == 3) {
				String block = null;
				try {
					block = findMaterial(args[1]);
				}
				catch(IllegalArgumentException iae) {
					sender.sendMessage(ChatColor.RED + "Please use a real block ID/Name!");
				}
				//List<String> approveditems = this.getConfig().getStringList("Approved-Items");
				
				
				if(args[0].equalsIgnoreCase("add")) {
					String amt = args[2];
					try{
						int intamt = Integer.parseInt(amt);
						if(intamt == 0) {
							sender.sendMessage(ChatColor.RED + "You can't add nothing to your total!");
							return false;
						}
					}
					catch(NumberFormatException nfe) {
						sender.sendMessage(ChatColor.RED + "The amount must be a number!");
						sender.sendMessage(ChatColor.RED + "Usage: /order add <block> <amount>");
						return false;
					}
					//Integer price = this.getConfig().getInt("Item." + block + ".Price");
					int number = 5;
					if(number != 5) {
						return false;
					}
					else {
						if(register.containsKey(name)) {
							ArrayList<String> registerlist = register.get(name);
							for(String blockamt : registerlist) {
								if(blockamt.startsWith(block)) {
									String[] splitblockamt = blockamt.split("@");
									
									Integer splitamt = Integer.parseInt(splitblockamt[1]);
									splitamt = splitamt + Integer.parseInt(amt);
									String splitamtstring = splitamt.toString();
									//blockamt
									register.remove(name);
									String stringtoadd = block + "@" + splitamtstring;
									Collections.replaceAll(registerlist, blockamt, stringtoadd);
									register.put(name, registerlist);
									double price = this.getConfig().getDouble("Prices." + findMaterial(args[1]));
									String moneysign = this.getConfig().getString("Money-Sign");
									sender.sendMessage(ChatColor.GREEN + "Added " + amt + " " + block + " to the order! @ " + moneysign + Double.toString(price) + " each");
									break;
								}
								else {
									if(blockamt == registerlist.get(registerlist.size() - 1)) {
										registerlist.add(block + "@" + amt);
										register.remove(name);
										register.put(name, registerlist);
										double price = this.getConfig().getDouble("Prices." + findMaterial(args[1]));
										String moneysign = this.getConfig().getString("Money-Sign");
										sender.sendMessage(ChatColor.GREEN + "Added " + amt + " " + block + " to the order! @ " + moneysign + Double.toString(price) + " each");
										break;
									}
								}
							}
						}
						else {
							ArrayList<String> newregisterlist = new ArrayList<String>();
							newregisterlist.add(block + "@" + amt);
							register.put(name, newregisterlist);
							double price = this.getConfig().getDouble("Prices." + findMaterial(args[1]));
							String moneysign = this.getConfig().getString("Money-Sign");
							sender.sendMessage(ChatColor.GREEN + "Added " + amt + " " + block + " to the order! @ " + moneysign + Double.toString(price) + " each");
						}
					}
				}
				else if(args[0].equalsIgnoreCase("set")) {
					String amt = args[2];
					try{
						Integer.parseInt(amt);
					}
					catch(NumberFormatException nfe) {
						sender.sendMessage(ChatColor.RED + "The amount must be a number!");
						sender.sendMessage(ChatColor.RED + "Usage: /order set <block> <amount>");
						return false;
					}
					if(register.containsKey(name)) {
						ArrayList<String> registerlistset = register.get(name);
						for(String setamt : registerlistset) {
							if(setamt.startsWith(block)) {
								String toadd = block + "@" + amt;
								if(Integer.parseInt(amt) == 0) {
									registerlistset.remove(setamt);
									if(registerlistset.isEmpty()) {
										register.remove(name);
									}
								}
								else {
									Collections.replaceAll(registerlistset, setamt, toadd);
								}
								register.remove(name);
								register.put(name, registerlistset);
								
								sender.sendMessage(ChatColor.GREEN + "Set the amount of " + block + " to " + amt + "!");
								break;
							}
							else if(setamt == registerlistset.get(registerlistset.size() - 1)) {
								if(Integer.parseInt(amt) == 0) {
									registerlistset.remove(setamt);
									if(registerlistset.isEmpty()) {
										register.remove(name);
									}
								}
								else {
									registerlistset.add(block + "@" + amt);
								}
								sender.sendMessage(ChatColor.GREEN + "Set the amount of " + block + " to " + amt + "!");
								break;
							}
						}
						if(registerlistset.isEmpty()) {
							register.remove(name);
						}
					}
					else {
						if(!(Integer.parseInt(amt) == 0)) {
							ArrayList<String> newregisterlistset = new ArrayList<String>();
							newregisterlistset.add(block + "@" + amt);
							register.put(name, newregisterlistset);
							sender.sendMessage(ChatColor.GREEN + "Set the amount of " + block + " to " + amt + "!");
						}
						else {
							sender.sendMessage(ChatColor.GREEN + "Set the amount of " + block + " to " + amt + "!");
						}
					}
				}
				
				else if(args[0].equalsIgnoreCase("subtract")) {
					String amt = args[2];
					try{
						Integer.parseInt(amt);
					}
					catch(NumberFormatException nfe) {
						sender.sendMessage(ChatColor.RED + "The amount must be a number!");
						sender.sendMessage(ChatColor.RED + "Usage: /order subtract <block> <amount>");
						return false;
					}
					if(register.containsKey(name)) {
						ArrayList<String> subtractregisterlist = register.get(name);
						for(String subtractitem : subtractregisterlist) {
							if(subtractitem.startsWith(block)) {
								String[] splitsub = subtractitem.split("@");
								
								Integer itemamt = Integer.parseInt(splitsub[1]);
								itemamt = itemamt - Integer.parseInt(amt);
								
								subtractregisterlist.remove(subtractitem);
								
								if(itemamt != 0) {
									subtractregisterlist.add(block + "@" + itemamt);
								}
								
								register.remove(name);
								register.put(name, subtractregisterlist);
								
								sender.sendMessage(ChatColor.GREEN + "Removed " + args[2] + " " + block + " from the order!");
								
								break;
							}
							else if(subtractitem == subtractregisterlist.get(subtractregisterlist.size() - 1)) {
								sender.sendMessage(ChatColor.RED + "You do not have any of that item in your total!");
							}
						}
					}
					else {
						sender.sendMessage(ChatColor.RED + "You do not have anything in your total right now.");					}
				}
				else sender.sendMessage(ChatColor.RED + "Please type /order to get the available commands!");
			}
			else {
				if(args.length >= 4) {
					sender.sendMessage(ChatColor.RED + "Please type /order to get the available commands!");
				}
			}
				
		}
		return false;
	}
}