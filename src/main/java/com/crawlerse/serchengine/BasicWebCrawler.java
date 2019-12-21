package com.crawlerse.serchengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(noRollbackFor = Exception.class)
public class BasicWebCrawler {

	@Autowired
	private LinkRepository linkrepo;

	private HashSet<String> links = new HashSet<String>();;
	private Set<Set<String>> articles = new HashSet<>();
	private Set<String> result = new LinkedHashSet<>();
	// static int count =0;

//    static {
//        links = new HashSet<String>();
//        getPageLinks("https://www.geeksforgeeks.org/");
//        
//    }

	@Transactional(noRollbackFor = Exception.class)
	public void getPageLinks(String URL) {
		// 4. Check if you have already crawled the URLs
		// (we are intentionally not checking for duplicate content in this example)
		List<LinkModel> urlslist = new ArrayList<>();
		if (!links.contains(URL)) {
			try {
				// 4. (i) If not add it to the index
				if (links.add(URL)) {

					LinkModel lm = new LinkModel();
					lm.setLink(URL);
					urlslist.add(lm);

				}

				// 2. Fetch the HTML code
				Document document = Jsoup.connect(URL).userAgent("Mozilla").get();
				// 3. Parse the HTML to extract links to other URLs
				Elements linksOnPage = document.select("a[href^=\"https://www.geeksforgeeks.org/\"]");

				// 5. For each extracted URL... go back to Step 4.
				for (Element page1 : linksOnPage) {
					if (urlslist.size() > 2800) {
						break;
					}

					// getPageLinks(page.attr("abs:href"));
					String URL1 = page1.attr("abs:href");
					if (links.add(URL1)) {

						LinkModel lm = new LinkModel();
						lm.setLink(page1.attr("abs:href"));
						linkrepo.save(lm);
						urlslist.add(lm);
						System.out.println(page1.attr("abs:href"));
					}

					// 2. Fetch the HTML code
					Document document1 = Jsoup.connect(URL1).userAgent("Mozilla").get();
					// 3. Parse the HTML to extract links to other URLs
					Elements linksOnPage1 = document1.select("a[href^=\"https://www.geeksforgeeks.org/\"]");

					for (Element page11 : linksOnPage1) {
						if (urlslist.size() > 2800) {
							break;
						}
						String URL2 = page11.attr("abs:href");
						if (links.add(URL2)) {

							LinkModel lmm = new LinkModel();
							lmm.setLink(page11.attr("abs:href"));
							urlslist.add(lmm);
							// linkrepo.save(lmm);

						}

						// 2. Fetch the HTML code
						Document document2 = Jsoup.connect(URL2).userAgent("Mozilla").get();
						// 3. Parse the HTML to extract links to other URLs
						Elements linksOnPage2 = document2.select("a[href^=\"https://www.geeksforgeeks.org/\"]");

						// getPageLinks(page.attr("abs:href"));
						for (Element page2 : linksOnPage2) {
							if (urlslist.size() > 2800) {
								break;
							}
							String URL22 = page2.attr("abs:href");
							if (links.add(URL22)) {

								LinkModel lm = new LinkModel();
								lm.setLink(page2.attr("abs:href"));
								urlslist.add(lm);

							}

						}

					}
				}

				for (LinkModel link : urlslist) {
					linkrepo.save(link);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(
						"No data Found.. @@@ size " + urlslist.size() + " Mesage error: " + e.getMessage());
			}

		}

	}

	// @Cacheable(value = "articles2", key = "#str")
	public Set<String> getArticles2(String str) {

		List<LinkModel> list = new ArrayList<>();
		String str1 = "";
		String str2 = "";
		if (str.contains(" ")) {
			String strarr[] = str.split(" ");
			str1 = strarr[0].trim();
			str2 = strarr[1].trim();
			list = linkrepo.findByLinkLikeMultipleIgnoreCase(str1, str2);
		} else {
			list = linkrepo.findByLinkLikeIgnoreCase(str);
		}

		Set<String> se = new HashSet<String>();
		for (LinkModel lm : list) {
			se.add(lm.getLink());
		}
		return se;
	}

	@Cacheable(value = "articles", key = "#str")
	public Set<String> getArticles(String str) {
		String input = str;
		String inputt = str.substring(0, 1).toUpperCase() + str.substring(1);
		input = inputt + "|" + input.toLowerCase() + "|" + input.toUpperCase();
		String inputregex = "^.*?(" + input + ").*$";

		// System.out.println("REGEX @@@@@@@@@@@@@ "+inputregex);
		links.forEach(x -> {
			Document document;
			try {
				document = Jsoup.connect(x).userAgent("Mozilla").get();
				Elements articleLinks = document.select("a[href^=\"http://www.geeksforgeeks.org/\"]");
				for (Element article : articleLinks) {
					// Only retrieve the titles of the articles that contain Java 8
					if (article.text().matches(inputregex)) {

						// Remove the comment from the line below if you want to see it running on your
						// editor,
						// or wait for the File at the end of the execution
						// System.out.println(article.attr("abs:href"));

						Set<String> temporary = new HashSet<>();
						temporary.add(article.text()); // The title of the article
						String res = article.attr("abs:href");
						temporary.add(res); // The URL of the article
						articles.add(temporary);
						result.add(res);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
			// System.out.println("@@@ Artc " + result);
		});

		return result;
	}
//    public static void main(String[] args) {
//        //1. Pick a URL from the frontier
//      BasicWebCrawler bwc =  new BasicWebCrawler();
//      articles = new HashSet<>();
//      result = new LinkedHashSet<>();
//      bwc.getArticles("java");
//      System.out.println(result);
//      
//    }

}