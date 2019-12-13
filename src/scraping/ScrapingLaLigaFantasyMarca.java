package scraping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ScrapingLaLigaFantasyMarca {

	public static final String xmlFilePath = "xmlfile.xml";

	public static void main(String[] args) throws ParserConfigurationException, TransformerConfigurationException {
		// TODO Auto-generated method stub

		String url = "https://coinmarketcap.com/";

		String file = "jornada01.html";
		int contador = 0;

		File input = new File("data/" + file);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//tomar numero de la jornada
		String posJornada = file.substring(7, 9);
		System.out.println(posJornada + "jornada----*******----jornada");
		// elemento raiz
//		Document doc = docBuilder.newDocument();
		Element jornadaElement = doc.createElement("jornada");
		doc.appendChild(jornadaElement);
		jornadaElement.attr("nro", posJornada);

//		if (getStatusConnectionCode(url) == 200) {
		if (getStatusFile(file) == 1) {

//			Document documento = getHtmlDocument(url);
			Document documento = getHtmlFileToDocument(file);

//			Analizando el score del juego
			Elements matchesContainer = documento.select("div.matches-container__match");
			System.out.println(matchesContainer.size());

			for (Element element : matchesContainer) {
				jornadaElement.appendChild(extractGame(element, doc));

			}

		}

		// nombre del fichero
		Date fecha = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		System.out.println("Hora y fecha: " + hourdateFormat.format(fecha));
		String nombreFichero = hourdateFormat.format(fecha);

		// escribimos el contenido en un archivo .xml
		String ruta = "dataXML\\";

		BufferedWriter writer = null;
		try {
			//writer = new BufferedWriter(new FileWriter(ruta + nombreFichero + ".xml"));
			 writer = new BufferedWriter
				    (new OutputStreamWriter(new FileOutputStream(ruta + nombreFichero + ".xml"), StandardCharsets.UTF_8));
			System.out.println(jornadaElement.outerHtml());
			writer.write(jornadaElement.outerHtml());

		} catch (IOException e) {
			System.out.println("error");
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("File saved!");

	}

	/**
	 * Con esta método compruebo el Status code de la respuesta que recibo al hacer
	 * la petición EJM: 200 OK 300 Multiple Choices 301 Moved Permanently 305 Use
	 * Proxy 400 Bad Request 403 Forbidden 404 Not Found 500 Internal Server Error
	 * 502 Bad Gateway 503 Service Unavailable
	 * 
	 * @param url
	 * @return Status Code
	 */
	public static int getStatusConnectionCode(String url) {

		Response response = null;

		try {
			response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
		}
		return response.statusCode();
	}

	/**
	 * Con este método devuelvo un objeto de la clase Document con el contenido del
	 * HTML de la web que me permitirá parsearlo con los métodos de la librelia
	 * JSoup
	 * 
	 * @param url
	 * @return Documento con el HTML
	 */
	public static Document getHtmlDocument(String url) {

		Document doc = null;
		try {
			doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
		}
		return doc;
	}

	public static int getStatusFile(String file) {
		return 1;
	}

	public static Document getHtmlFileToDocument(String file) {

		File input = new File("data/" + file);
		Document doc = null;
		try {
			doc = Jsoup.parse(input, "UTF-8", "http://example.com/");
		} catch (IOException ex) {
			System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
		}
		return doc;
	}
	private static Element extractPlayerLocal(Element element2, Document doc, DataOrder orden) {
		int contador;
		Elements playerAux = element2.select("div > span");
		contador = 0;
		Element playerElement = doc.createElement("player");

		for (Element element3 : playerAux) {
			System.out.println(element3.text());
			contador++;
			switch (contador) {
			case 1:
				if(orden == DataOrder.PointsNameEmpty) {
					playerElement.attr("puntos", element3.text());
				}
				break;
			case 2:
				Elements dataAux = element3.select("span > label");
				if(orden == DataOrder.PointsNameEmpty) {
					playerElement.attr("name", dataAux.get(0).text());
					playerElement.attr("pos", dataAux.get(1).text());
				}	else {
					playerElement.attr("name", dataAux.get(1).text());
					playerElement.attr("pos", dataAux.get(0).text());
				}
				
				break;
			case 3:
				if(orden == DataOrder.EmptyNamePoints) {
					playerElement.attr("puntos", element3.text());
				}
				
				break;
			}
		}
		
		return playerElement;
	}
	private static Element extractGame(Element element, Document doc) {
		int contador;
		Element gameElement = doc.createElement("game");

		Elements matchTeamLocal = element
				.select("div.match-block.text-center > div.match-block__local > span.match-block__team-name");
		System.out.println(matchTeamLocal.get(0).text());
		gameElement.attr("aa", matchTeamLocal.get(0).text());
		Element teamElementLocal = doc.createElement("team");
		gameElement.appendChild(teamElementLocal);
		teamElementLocal.attr("name", matchTeamLocal.get(0).text());

		Elements matchResult = element.select("div.match-block.text-center > div > span.match-block__goals");
		System.out.println(matchResult.get(0).text());
		gameElement.attr("bb", matchResult.get(0).text());

		Elements matchTeamVisitor = element
				.select("div.match-block.text-center > div.match-block__visitor > span.match-block__team-name");
		System.out.println(matchTeamVisitor.get(0).text());
		gameElement.attr("cc", matchTeamVisitor.get(0).text());
		Element teamElementVisitor = doc.createElement("team");
		gameElement.appendChild(teamElementVisitor);
		teamElementVisitor.attr("name", matchTeamVisitor.get(0).text());

		Elements matchPlayers = element.select("div.match-stats >" + "div.match-stats__local >" + "div");
		for (Element element2 : matchPlayers) {
			teamElementLocal.appendChild(extractPlayerLocal(element2, doc, DataOrder.EmptyNamePoints));
		}
		
		Elements matchPlayersVisiator = element.select("div.match-stats >" + "div.match-stats__visitor >" + "div");
		for (Element element2 : matchPlayersVisiator) {
			teamElementVisitor.appendChild(extractPlayerLocal(element2, doc, DataOrder.PointsNameEmpty));
		}

		return gameElement;
	}

	private static Element extractPlayer(Element element, Document doc) {
		Element gameElement = doc.createElement("game");

		Elements matchTeamLocal = element
				.select("div.match-block.text-center > div.match-block__local > span.match-block__team-name");
		System.out.println(matchTeamLocal.get(0).text());
		gameElement.attr("aa", matchTeamLocal.get(0).text());

		Elements matchResult = element.select("div.match-block.text-center > div > span.match-block__goals");
		System.out.println(matchResult.get(0).text());
		gameElement.attr("bb", matchResult.get(0).text());

		Elements matchTeamVisitor = element
				.select("div.match-block.text-center > div.match-block__visitor > span.match-block__team-name");
		System.out.println(matchTeamVisitor.get(0).text());
		gameElement.attr("cc", matchTeamVisitor.get(0).text());

		return gameElement;
	}

	private static Element extractOffensiveHtmlToXml(Elements elementos, Document doc) {

		Element players = doc.createElement("batters");

		for (Element elem : elementos) {

			// para no tomar la primera entrada que tiene el encabezado
//			if (!(elem.equals(elementos.first()))) {
//				System.out.println("ok");

			Element player = doc.createElement("player");
			players.appendChild(player);
			Integer contador = 0;
			Elements playerData = elem.select("td");
			for (Element playerElement : playerData) {
				contador++;

				// tomar el data-label
				// String dataLabel = playerElement.attr("data-label");

				String attrName = "";
				String cadena = "";

				switch (contador) {
				case 1:
					attrName = "name";
					cadena = playerElement.text();
					cadena = extractElementBefore(cadena);
					// cadena = cadena2.trim();
					Elements playerDataIds = elem.select("a");

					if (!playerDataIds.isEmpty()) {
						Element playerDataId = playerDataIds.get(0);
						String playerDataIdA = playerDataId.attr("href");
						// atributo del player
						player.attr("id", extractIdLink(playerDataIdA));
					}
					break;
				case 2:
					attrName = "ab";
					cadena = playerElement.text();
					break;
				case 3:
					attrName = "r";
					cadena = playerElement.text();
					break;
				case 4:
					attrName = "h";
					cadena = playerElement.text();
					break;
				case 5:
					attrName = "rbi";
					cadena = playerElement.text();
					break;
				case 6:
					attrName = "bb";
					cadena = playerElement.text();
					break;
				case 7:
					attrName = "sb";
					cadena = playerElement.text();
					break;
				case 8:
					attrName = "so";
					cadena = playerElement.text();
					break;
				case 9:
					attrName = "lob";
					cadena = playerElement.text();
					break;
				case 10:
					attrName = "ave";
					cadena = playerElement.text();
					break;
				case 11:
					attrName = "obp";
					cadena = playerElement.text();
					break;
				case 12:
					attrName = "slg";
					cadena = playerElement.text();
					break;
				case 13:
					attrName = "ops";
					cadena = playerElement.text();
					break;
				}
				// String cadena = playerElement.text();

				// atributo del player
//					Attr attr = doc.createAttribute(attrName);
//					attr.setValue(cadena);
//					player.setAttributeNode(attr);
				player.attr(attrName, cadena.trim());

			}
//			}

		}
		return players;
	}

	private static String extractIdLink(String cadena) {
		String[] cadenaSplit = cadena.split("player/");
		return cadenaSplit[1];
	}

	private static String extractElementBefore(String cadena) {
		while (!Character.isLetter(cadena.charAt(0))) {
			cadena = cadena.substring(1, cadena.length());
		}
		// String[] cadenaSplit = cadena.split(";");
		// return cadenaSplit[cadenaSplit.length-1];
		return cadena;
	}

	private static Element extractPitchHtmlToXml(Elements elementos, Document doc) {

		Element players = doc.createElement("pitchers");

		for (Element elem : elementos) {

			// para no tomar la primera entrada que tiene el encabezado
//			if (!(elem.equals(elementos.first()))) {
//				System.out.println("ok");

			Element player = doc.createElement("player");
			players.appendChild(player);
			Integer contador = 0;
			String cadena = "";
			Elements playerData = elem.select("td");
			for (Element playerElement : playerData) {
				contador++;
				String attrName = "";
				switch (contador) {
				case 1:
					attrName = "name";
					cadena = playerElement.text();
					cadena = extractElementBefore(cadena);
					// cadena = cadena2.trim();
					Elements playerDataIds = elem.select("a");

					if (!playerDataIds.isEmpty()) {
						Element playerDataId = playerDataIds.get(0);
						String playerDataIdA = playerDataId.attr("href");
						// atributo del player
						player.attr("id", extractIdLink(playerDataIdA));
					}
					break;
				case 2:
					attrName = "ip";
					cadena = playerElement.text();
					break;
				case 3:
					attrName = "h";
					cadena = playerElement.text();
					break;
				case 4:
					attrName = "r";
					cadena = playerElement.text();
					break;
				case 5:
					attrName = "er";
					cadena = playerElement.text();
					break;
				case 6:
					attrName = "bb";
					cadena = playerElement.text();
					break;
				case 7:
					attrName = "k";
					cadena = playerElement.text();
					break;
				case 8:
					attrName = "hr";
					cadena = playerElement.text();
					break;
				case 9:
					attrName = "pc-st";
					cadena = playerElement.text();
					break;
				case 10:
					attrName = "era";
					cadena = playerElement.text();
					break;
//					case 11: attrName = "bk";
//					break;
//					case 12: attrName = "inn";
//					break;
				}
//				String cadena = playerElement.text();

				// atributo del player
				player.attr(attrName, cadena.trim());
			}
//			}

		}
		return players;
	}
}
