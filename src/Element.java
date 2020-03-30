import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Element
{
	public static final double ZETTA = Math.pow(10, 21);
	public static final double U = 1.66053886 * Math.pow(10, -27);
	public static final double PICO = Math.pow(10, 10);

	private int atomicNumber;
	private String symbol;
	private String name;
	private int empiricalAtomicRadius;
	private int calculatedAtomicRadius;
	private double atomsPerVolume;
	private double density;
	private double weight;

	public Element(int atomicNumber, String symbol, String name, int empiricalAtomicRadius, int calculatedAtomicRadius)
	{
		this.atomicNumber = atomicNumber;
		this.symbol = symbol;
		this.name = name;
		this.empiricalAtomicRadius = empiricalAtomicRadius;
		this.calculatedAtomicRadius = calculatedAtomicRadius;
	}

	public int getAtomicNumber()
	{
		return atomicNumber;
	}

	public void setAtomicNumber(int atomicNumber)
	{
		this.atomicNumber = atomicNumber;
	}

	public String getSymbol()
	{
		return symbol;
	}

	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getEmpiricalAtomicRadius()
	{
		return empiricalAtomicRadius;
	}

	public void setEmpiricalAtomicRadius(int empiricalAtomicRadius)
	{
		this.empiricalAtomicRadius = empiricalAtomicRadius;
	}

	public int getCalculatedAtomicRadius()
	{
		return calculatedAtomicRadius;
	}

	public void setCalculatedAtomicRadius(int calculatedAtomicRadius)
	{
		this.calculatedAtomicRadius = calculatedAtomicRadius;
	}

	public double getAtomsPerVolume()
	{
		return atomsPerVolume;
	}

	public void setAtomsPerVolume(double atomsPerVolume)
	{
		this.atomsPerVolume = atomsPerVolume;
	}

	public double getDensity()
	{
		return density;
	}

	public void setDensity(double density)
	{
		this.density = density;
	}

	public double getWeight()
	{
		return weight;
	}

	public void setWeight(double weight)
	{
		this.weight = weight;
	}

	public double calculateEmpiricalVolume()
	{
		return 4 * Math.PI * Math.pow(this.calculatedAtomicRadius / PICO, 3) / 3;
	}

	public double calculateCalculatedVolume()
	{
		return 4 * Math.PI * Math.pow(this.empiricalAtomicRadius / PICO, 3) / 3;
	}

	public double calculateEmpiricalDensity()
	{
		return this.weight / this.calculateEmpiricalVolume();
	}

	public double calculateCalculatedDensity()
	{
		return this.weight / this.calculateCalculatedVolume();
	}

	public double calculateEmpiricalDistanceBetweenAtoms()
	{
		if(this.weight <= 0 || this.density <= 0 || this.empiricalAtomicRadius <= 0) return 0;
		double r = Math.pow(3 * this.weight / (4 * Math.PI * this.density), 1d / 3d);
		return 2 * (r - this.empiricalAtomicRadius / PICO);
	}

	public double calculateCalculatedDistanceBetweenAtoms()
	{
		if(this.weight == 0 || this.density == 0 || this.calculatedAtomicRadius == 0) return 0;
		double r = Math.pow(3 * this.weight / (4 * Math.PI * this.density), 1d / 3d);
		return 2 * (r - this.calculatedAtomicRadius / PICO);
	}

	public static void main(String[] args)
	{
		ArrayList<Element> elements = new ArrayList<Element>();
		String atomicRadiiStr = "./src/atomicRadii.csv", atomicWeightStr = "./src/atomicWeight.csv",
				numericDensitiesStr = "./src/numericDensities.csv";
		File atomicRadii = new File(atomicRadiiStr), atomicWeight = new File(atomicWeightStr), numericDensities =
				new File(numericDensitiesStr);

		try (BufferedReader arbr = new BufferedReader(new FileReader(atomicRadii));
		     BufferedReader ndbr = new BufferedReader(new FileReader(numericDensities));
		     BufferedReader awbr = new BufferedReader(new FileReader(atomicWeight)))
		{
			String[] seq = arbr.readLine().split(",");
			while (arbr.ready())
			{
				String[] elementStr = arbr.readLine().split(",");
				elementStr[3] = elementStr[3].equals("no data") ? "0" : elementStr[3];
				elementStr[4] = elementStr[4].equals("no data") ? "0" : elementStr[4];
				elements.add(new Element(Integer.parseInt(elementStr[0]), elementStr[1], elementStr[2],
				                         Integer.parseInt(elementStr[3]), Integer.parseInt(elementStr[4])));
			}

			seq = ndbr.readLine().split(",");
			while (ndbr.ready())
			{
				String[] elementStr = ndbr.readLine().split(",");
				elementStr[3] = elementStr[3].replaceAll("[A-Za-z()]+", "");
				elementStr[2] = elementStr[2].replaceAll("[A-Za-z()]+", "");
				if (elementStr[3].isEmpty()) elementStr[3] = "0";
				if (elementStr[2].isEmpty()) elementStr[2] = "0";
				elements.get(Integer.parseInt(elementStr[4]) - 1).setAtomsPerVolume(Double.parseDouble(elementStr[3]));
				elements.get(Integer.parseInt(elementStr[4]) - 1).setDensity(Double.parseDouble(elementStr[2]));
			}

			Pattern dash = Pattern.compile("[-(]");
			while (awbr.ready())
			{
				String[] elementStr = awbr.readLine().split(",");
				Matcher matcher = dash.matcher(elementStr[4]);
				elementStr[4] = matcher.find() ? elementStr[4].substring(1, matcher.start()) : elementStr[4];
				elementStr[4] = elementStr[4].replaceAll("[A-Za-z ]+", "");
				if (elementStr[4].isEmpty())
				{
					elementStr[4] = "0";
				}
				elements.get(Integer.parseInt(elementStr[0]) - 1).setWeight(Double.parseDouble(elementStr[4]) * Element.U * 1000);
			}

			System.out.println("Nr.\tDensity\t\tCalculated dens\t\tEmpirical dens\t\tCalc distance\t\t\tEmp distance");
			for (Element e : elements)
			{
				System.out.println(e.getAtomicNumber() + "\t" + e.getDensity() + "\t" + e.calculateCalculatedDensity() + "\t" + e.calculateEmpiricalDensity() + "\t" + e.calculateCalculatedDistanceBetweenAtoms() + "\t" + e.calculateEmpiricalDistanceBetweenAtoms());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
