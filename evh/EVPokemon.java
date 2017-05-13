package evh;

public class EVPokemon implements Comparable<EVPokemon> {

	private String name;
	private int[] evYield;
	
	public EVPokemon(String n, int[] evs)
	{
		name = n;
		evYield = evs;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int[] getEVYield()
	{
		return evYield;
	}
	
	@Override
	public String toString()
	{
		String yieldString = "";
		for(int i = 0; i < evYield.length; i++)
			yieldString += evYield[i] + " ";
		
		return name + " " + yieldString;
	}
	
	public int compareTo(EVPokemon other)
	{
		return name.compareTo(other.getName());
	}
	
}
