package com.pratik.sainsbury;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Hello world!
 *
 */
public class WebScrapper {

	public static void main(String[] args) {

		float price = 0;
		float vat = 0;

		// JSON objects for storing results.
		JSONObject finalObject = new JSONObject();
		JSONObject result1 = new JSONObject();
		JSONArray resultArray = new JSONArray();

		// Creating document to store the parsed HTML file in DOM structure
		Document doc = null;

		try {
			doc = Jsoup.connect(
					"https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html")
					.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String title = doc.title();

		Elements links = doc.getElementsByClass("productNameAndPromotions").select("a[href]");

		for (Element element : links) {
			String url = element.attr("href").replace("../../../../../../",
					"https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/");
			// System.out.println(url);
			try {
				JSONObject json = new JSONObject();

				Document productDoc = Jsoup.connect(url).get();
				String productName = productDoc.getElementsByClass("productTitleDescriptionContainer").text();
				json.put("title", productName);
				String price1 = productDoc.getElementsByClass("pricePerUnit").first().text().toString();

				String kcal_per_100g = null;

				String energy = null;
				if (productDoc.getElementsByClass("tableRow0").hasText()) {
					energy = productDoc.getElementsByClass("tableRow0").first().text().toString();
					String[] kcal = energy.split(" ");
					kcal_per_100g = kcal[0].replaceAll("kcal", "").trim().toString();
					json.put("kcal_per_100g", kcal_per_100g);

				}
				String unit_price = price1.replaceAll("£", "").replaceAll("/unit", "").trim();
				json.put("unit_price", unit_price);
				price += Float.parseFloat(unit_price);
				String description = productDoc.getElementsByClass("productText").select("p").first().text();
				json.put("description", description);
				resultArray.add(json);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Consolidating every thing in a single JSON object.
		result1.put("gross", price);
		result1.put("vat", price * 0.2);

		finalObject.put("results", resultArray);
		finalObject.put("total", result1);

		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		String prettyJsonString = gson.toJson(finalObject);

		System.out.println(prettyJsonString);

	}
}
