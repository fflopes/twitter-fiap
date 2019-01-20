package br.com.fiap.atividadefinal.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.fiap.atividadefinal.connection.AtividadeFinalConnection;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class AtividadeFinalService {
	
	private String hashtag;
	private List<Status> tweets = new ArrayList<>();
	private DateFormat df = new SimpleDateFormat("dd");

	public AtividadeFinalService(String hashtag) throws Exception {
		this.hashtag = hashtag;		
		pesquisaTweetsSemanal();
	}

	private void pesquisaTweetsSemanal() throws Exception {
		Twitter twitter = AtividadeFinalConnection.getTwitter();
		Query query = new Query(this.hashtag);
		
		LocalDate dataAtual= LocalDate.now();
		LocalDate seteDias = LocalDate.now().minusWeeks(1);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		query.setSince(dtf.format(seteDias));
		query.setUntil(dtf.format(dataAtual));

		try {
			QueryResult result = twitter.search(query);
			while (result.hasNext()) {
				query = result.nextQuery();
				this.tweets.addAll(result.getTweets());

				result = twitter.search(query);
			}
		} catch (TwitterException e) {
			throw new Exception("Erro  Tweets");
		}
	}

	public Map<String, Long> getQuantidadeTweetsDiario() {

		//retweets
		Map<String, Long> tweetsPorDia = this.tweets.stream()
				.collect(Collectors.groupingBy(d -> df.format(d.getCreatedAt()), Collectors.counting()));

		// map ordenado
		Map<String, Long> tweetsDiariosSorted = new LinkedHashMap<>();
		tweetsPorDia.entrySet().stream()
				.sorted((twee1, twee2) -> twee1.getKey().compareTo(twee2.getKey()))
				.forEachOrdered(t -> tweetsDiariosSorted.put(t.getKey(), t.getValue()));

		return tweetsDiariosSorted;
	}

	public Map<String, Long> getQuantidadeRetweetsDiario() {
		
		//Group by por data somando os retweets
		Map<String, Long> retweetsDiario = this.tweets.stream()
				.collect(Collectors.groupingBy(d -> df.format(d.getCreatedAt()), Collectors.summingLong(Status::getRetweetCount)));
		
		//map ordenado
		Map<String, Long> retweetsDiarioSorted = new LinkedHashMap<>();
		retweetsDiario.entrySet().stream()
				.sorted((ret1, ret2) -> ret1.getKey().compareTo(ret2.getKey()))
				.forEachOrdered(t -> retweetsDiarioSorted.put(t.getKey(), t.getValue()));

		return retweetsDiarioSorted;
	}

	public Map<String, Long> getQuantidadeFavoritacoes() {

		// Group By Favoritações
		Map<String, Long> FavoritesPorDia = this.tweets.stream()
				.collect(Collectors.groupingBy(d -> df.format(d.getCreatedAt()), Collectors.summingLong(Status::getFavoriteCount)));
		
		//map ordenado
		Map<String, Long> favoritesDiarioSorted = new LinkedHashMap<>();
		FavoritesPorDia.entrySet().stream()
				.sorted((fav1, fav2) -> fav1.getKey().compareTo(fav2.getKey()))
				.forEachOrdered(t -> favoritesDiarioSorted.put(t.getKey(), t.getValue()));

		return favoritesDiarioSorted;
	}	
	
	public String getPrimeiroAutor() {

		// Retornando o primeiro autor do Tweet por ordem alfabética
		Status firstName = this.tweets.stream()
				.min((aut1, aut2) -> aut1.getUser().getName().compareToIgnoreCase(aut2.getUser().getName()))
				.get();
		return firstName.getUser().getName();
	}

	public String getUltimoAutor() {
		
		//Retornando último autor por ordem alfabética
		Status lastName = this.tweets.stream()
				.max((aut1, aut2) -> aut1.getUser().getName().compareToIgnoreCase(aut2.getUser().getName()))
				.get();
		return lastName.getUser().getName();
	}
	
	public Date getDataRecente() {
		return this.tweets.stream()
				.min((data1, data2) -> data1.getCreatedAt().compareTo(data2.getCreatedAt()))
				.get()
				.getCreatedAt();
	}
	
	public Date getDataAnterior() {		
		return this.tweets.stream()
				.max((data1, data2) -> data1.getCreatedAt().compareTo(data2.getCreatedAt()))
				.get()
				.getCreatedAt();
	}

	public boolean isEmpty() {
		return tweets.isEmpty();
	}
}
