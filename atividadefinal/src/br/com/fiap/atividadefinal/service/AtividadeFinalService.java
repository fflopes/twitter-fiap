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
		tweetsUltimaSemana();
	}

	private void tweetsUltimaSemana() throws Exception {
		Twitter twitter = AtividadeFinalConnection.getTwitter();
		Query query = new Query(this.hashtag);
		
		LocalDate hoje = LocalDate.now();
		LocalDate seteDiasAntes = LocalDate.now().minusWeeks(1);
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		query.setSince(dtf.format(seteDiasAntes));
		query.setUntil(dtf.format(hoje));
		
		try {
			QueryResult result = twitter.search(query);
			while (result.hasNext()) {
				query = result.nextQuery();
				this.tweets.addAll(result.getTweets());
				
				result = twitter.search(query);
			}
		} catch (TwitterException e) {
			throw new Exception("Erro ao buscar lista de Tweets");
		}
	}

	public Map<String, Long> getQuantidadeTweetsPorDia() {
		
		// Aplicando o group by por data somando os retweets
		Map<String, Long> tweetsPorDia = this.tweets.stream()
				.collect(Collectors.groupingBy(d -> df.format(d.getCreatedAt()), Collectors.counting()));
		
		// Criando o map ordenado
		Map<String, Long> tweetsPorDiaSorted = new LinkedHashMap<>();
		tweetsPorDia.entrySet().stream()
				.sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
				.forEachOrdered(t -> tweetsPorDiaSorted.put(t.getKey(), t.getValue()));

		return tweetsPorDiaSorted;
	}

	public Map<String, Long> getQuantidadeRetweetsPorDia() {
		
		// Aplicando o group by por data somando os retweets
		Map<String, Long> retweetsPorDia = this.tweets.stream()
				.collect(Collectors.groupingBy(d -> df.format(d.getCreatedAt()), Collectors.summingLong(Status::getRetweetCount)));
		
		// Criando o map ordenado
		Map<String, Long> retweetsPorDiaSorted = new LinkedHashMap<>();
		retweetsPorDia.entrySet().stream()
				.sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
				.forEachOrdered(t -> retweetsPorDiaSorted.put(t.getKey(), t.getValue()));

		return retweetsPorDiaSorted;
	}

	public Map<String, Long> getQuantidadeFavoritesPorDia() {

		// Aplicando o group by por data somando os Favorites
		Map<String, Long> FavoritesPorDia = this.tweets.stream()
				.collect(Collectors.groupingBy(d -> df.format(d.getCreatedAt()), Collectors.summingLong(Status::getFavoriteCount)));
		
		// Criando o map ordenado
		Map<String, Long> favoritesPorDiaSorted = new LinkedHashMap<>();
		FavoritesPorDia.entrySet().stream()
				.sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
				.forEachOrdered(t -> favoritesPorDiaSorted.put(t.getKey(), t.getValue()));

		return favoritesPorDiaSorted;
	}	
	
	public String getPrimeiroAutorPorNome() {
		
		// Exibindo o primeiro autor do Tweet por ordem alfabética
		Status firstName = this.tweets.stream()
				.min((s1, s2) -> s1.getUser().getName().compareToIgnoreCase(s2.getUser().getName()))
				.get();
		return firstName.getUser().getName();
	}
	
	public String getUltimoAutorPorNome() {
		
		// Exibindo o último autor da lista por ordem alfabética
		Status lastName = this.tweets.stream()
				.max((s1, s2) -> s1.getUser().getName().compareToIgnoreCase(s2.getUser().getName()))
				.get();
		return lastName.getUser().getName();
	}
	
	public Date getDataMenosRecente() {
		return this.tweets.stream()
				.min((s1, s2) -> s1.getCreatedAt().compareTo(s2.getCreatedAt()))
				.get()
				.getCreatedAt();
	}
	
	public Date getDataMaisRecente() {		
		return this.tweets.stream()
				.max((s1, s2) -> s1.getCreatedAt().compareTo(s2.getCreatedAt()))
				.get()
				.getCreatedAt();
	}
		
	public boolean isEmpty() {
		return tweets.isEmpty();
	}

}
