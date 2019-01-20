package br.com.fiap.atividadefinal.view;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import br.com.fiap.atividadefinal.connection.AtividadeFinalConnection;
import br.com.fiap.atividadefinal.service.AtividadeFinalService;
import twitter4j.Status;
import twitter4j.Twitter;

public class AtividadeFinal {
	
public static void main(String[] args) {
		
		String hashtag = "#java8";
		String mention = "@h_luizspfc";
		
		AtividadeFinalService atividadeFinalService = null;
		try {
			atividadeFinalService = new AtividadeFinalService(hashtag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		if (atividadeFinalService.isEmpty()) {				
			System.out.println("Não foi encontrada nenhuma informação");				
		} else {				
			StringBuilder tweet = new StringBuilder();
			
			tweet.append(hashtag + "\n");
			tweet.append("\n");
			
			tweet.append("Teste");
			
			//Mapeando os Tweets
			Map<String, Long> quantidadeTweetsPorDia = atividadeFinalService.getQuantidadeTweetsPorDia();
			quantidadeTweetsPorDia.forEach(
				(dia, quantidade) -> tweet.append(dia + ":" + quantidade + "\n")
			);
			long totalSemana = quantidadeTweetsPorDia.values().stream()
					.mapToLong(Long::longValue)
					.sum();
			tweet.append("Quantidade por dia de tweets da última semana.: " + totalSemana + "\n");
			tweet.append("\n");
			
			//Mapeando os Retweets
			Map<String, Long> quantidadeRetweetsPorDia = atividadeFinalService.getQuantidadeRetweetsPorDia();
			quantidadeRetweetsPorDia.forEach(
				(dia, quantidade) -> tweet.append(dia + ":" + quantidade + "\n")
			);
			totalSemana = quantidadeRetweetsPorDia.values().stream()
					.mapToLong(Long::longValue)
					.sum();
			tweet.append("Quatidade por Dia de Retweets: " + totalSemana + "\n");
			tweet.append("\n");
			
			//Mapeando as Favoritações
			Map<String, Long> quantidadeFavoritesPorDia = atividadeFinalService.getQuantidadeFavoritesPorDia();
			quantidadeFavoritesPorDia.forEach(
				(dia, quantidade) -> tweet.append(dia + ":" + quantidade + "\n")
			);
			totalSemana = quantidadeFavoritesPorDia.values().stream()
					.mapToLong(Long::longValue)
					.sum();
			tweet.append("Quantidade por dia de favoritações da última semana: " + totalSemana + "\n");
			tweet.append("\n");

			//Mostrando primeiro e último autor da lista
			tweet.append("Primeiro nome e o último nome\n");
			tweet.append(atividadeFinalService.getPrimeiroAutorPorNome() + "\n");
			tweet.append(atividadeFinalService.getUltimoAutorPorNome() + "\n");
			tweet.append("\n");
			
			//Mostrando primeira e última data da lista
			DateFormat df = new SimpleDateFormat("dd/MM");
			tweet.append("data mais recente e a menos recente\n");
			tweet.append(df.format(atividadeFinalService.getDataMaisRecente()) + "\n");
			tweet.append(df.format(atividadeFinalService.getDataMenosRecente()) + "\n");
			tweet.append("\n");
			
			tweet.append(mention);
			
			postaTweet(tweet.toString());
			

		}
	}

	public static void postaTweet(String tweet) {
		try {
			Twitter twitter = AtividadeFinalConnection.getTwitter();
			Status status = twitter.updateStatus(tweet);
			
			System.out.println("Tweet postado com sucesso!1");
			System.out.println();
			System.out.println(status.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
