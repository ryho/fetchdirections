import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.io.PrintWriter;
public class FetchDirections{
  public static void main (String args[]){
		System.out.println("\n\nPlease go to Google Maps (http://maps.google.com/), and get directions.");
		System.out.println("Then click the chain link icon, copy the link it gives you, and paste it right here:");
		try {
			// get text from command line
			Scanner console = new Scanner(System.in);
			PrintWriter fileOut = new PrintWriter("DIR.TXT");
			String input=console.nextLine();
			System.out.println("\n");
			// get the source and destination from the input
			int source=input.indexOf("saddr=")+6;
			int sourceEnd=input.indexOf("&",source);
			int dest=input.indexOf("daddr=")+6;
			int destEnd=input.indexOf("&",dest);
			//submit Google Maps API request for walking directions between the two locations in XML
			URL url = new URL("http://maps.googleapis.com/maps/api/directions/xml?origin=" + input.substring(source,sourceEnd) + "&destination=" + input.substring(dest,destEnd) + "&sensor=true&mode=walking");
			InputStream is = url.openStream();
			// start parsing the XML
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			NodeList steps = doc.getElementsByTagName("step");
			Node step,start;
			Element latlng;
			/* You could use this to output the starting coordinates
			Node step = steps.item(0);
			Node start = step.getFirstChild();
			while(!start.getNodeName().equals("start_location")){
				start=start.getNextSibling();
			}
			Element latlng=(Element)start;
			System.out.println(parse("lat",latlng));
			System.out.println(parse("lng",latlng));
			System.out.println(latlng.getElementsByTagName("lat").item(0).getTextContent());
			System.out.println(latlng.getElementsByTagName("lng").item(0).getTextContent());
			start = step.getFirstChild();*/
			char letter0, letter1, letter2;
			// for every step in the directions, get the GPS coordinates and the direction to turn
			for (int i = 0; i < steps.getLength(); i++) {
				// grab the text step
				step = steps.item(i);

				// get the html directions from it
				start = step.getFirstChild();
				while(!start.getNodeName().equals("html_instructions")){
					//this is likely a really poor way to do it, but the code runs so quickly that it doesn't matter
					start=start.getNextSibling();
				}
				// once it finds the XML element "html_instructions",
				// it attempts to read the directions.
				// the directions are in natural English, but tend to follow a few patterns
				// if it doesn't fit a patten that it knows, it will ask the user to read it
				// and determine which direction it says to go
				letter0 = start.getTextContent().charAt(0);
				letter1 = start.getTextContent().charAt(8);
				letter2 = start.getTextContent().charAt(10);
				//"Turn <b>right...", or "Slight <b>right"
				if((letter0 == 'T' && letter1=='r')|| (letter0 == 'S' && letter2=='r'))
					fileOut.println("R");
				//"Turn <b>left...", or "Slight <b>left"
				else if((letter0 == 'T' && letter1=='l')|| (letter0 == 'S' && letter2=='l'))
					fileOut.println("L");
				//"Head north..."
				else if(letter1=='n')// I don't have a compass on the vest, so the person has to figure out how to start
					System.out.println("Begin by heading north");
				else if(letter1=='s')
					System.out.println("Begin by heading south");
				else if(letter1=='e')
					System.out.println("Begin by heading east");
				else if(letter1=='w')
					System.out.println("Begin by heading west");
				// "Continue onto ..."
				else if(letter0=='C')
					fileOut.println("D");
				else{//else :(
					System.out.println("I did not recognize this instruction from Google Maps:\n"+start.getTextContent());
					System.out.println("Type L for Left, R for Right, D for straight, and U for U turn: ");
					input=console.nextLine();
					fileOut.println(input.charAt(0));
					System.out.println("");
				}
				// find the XML element containing the end location for the step
				start = step.getFirstChild();
				while(!start.getNodeName().equals("end_location")){
					start=start.getNextSibling();
				}
				// get the coordinates, save to text file
				latlng=(Element)start;
				fileOut.println(parse("lat",latlng));
				fileOut.println(parse("lng",latlng));
			}
			fileOut.println("F\n");// F means finished
			fileOut.flush();//finish writing to text file
			fileOut.close();// close the file
		} catch (Exception e){
			System.out.println("There was an error that triggered an exception :(");
		}
		System.out.println("\nPlease place the DIR.TXT file on your SD card and insert it into your vest.");
		System.out.println("Do not edit or rename the text file.\n");
	}
	// gets the directions, converts the number to a number that the Arduino will like
	// the Arduino GPS Module uses DDMM.MMMMM which is the two digit degree concatenated with the minutes.
	// this ignores the negative sign. If you happen to want to use this while crossing the equator 
	// or prime meridian, this would cause problems
	public static String parse(String input,Element latlng){
		input = latlng.getElementsByTagName(input).item(0).getTextContent();
		int index = (input.charAt(0)=='-')?4:3;
		input = input.substring(index-3,index-1)+((Integer)(Integer.parseInt(input.substring(index))*60)).toString();
		input = input.substring(0,4)+"."+input.substring(4);
		return input;
	}
}




