package br.com.fiap.atividadefinal.view;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Properties;

import br.com.fiap.atividadefinal.connection.AtividadeFinalConnection;
import br.com.fiap.atividadefinal.service.AtividadeFinalService;
import twitter4j.Status;
import twitter4j.Twitter;

public class AtividadeFinal {
	
	public static Properties getProp() throws IOException {
        Properties props = new Properties();
        FileInputStream file = new FileInputStream("./properties/config.properties");
        props.load(file);
        return props;
    }
	
	public static void main(String[] args) throws IOException {
		String hashtag;
		String diasQuantidades;
		String tweets;
		String retweets;
		String favoritacoes;
		String dataAntiga;
		String dataRecente;
		String primeiroAutor;
		String ultimoAutor;
		String endereco;
		
		Properties prop = getProp();
		
		hashtag=prop.getProperty("prop.hashtag");
		diasQuantidades= prop.getProperty("prop.dia.quantidade");
		tweets= prop.getProperty("prop.tweets");
		retweets= prop.getProperty("prop.retweets");
		favoritacoes= prop.getProperty("prop.favoritacoes");
		dataAntiga= prop.getProperty("prop.data.antiga");
		dataRecente = prop.getProperty("prop.data.recente");
		primeiroAutor = prop.getProperty("prop.autor.primeiro");
		ultimoAutor = prop.getProperty("prop.autor.utimo");
		endereco=prop.getProperty("prop.endereco");
		
		AtividadeFinalService atividadeFinalService = null;
		try {
			atividadeFinalService = new AtividadeFinalService(hashtag);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (atividadeFinalService.isEmpty()) {				
			System.out.println("Nenhum dado encontrado");				
		} else {				
			StringBuilder tweet = new StringBuilder();
			
			tweet.append(hashtag);			
			//Tweets
			tweet.append("\n"+tweets +"\n");
			Map<String, Long> quantidadeTweetsPorDia = atividadeFinalService.getQuantidadeTweetsDiario();
			tweet.append(diasQuantidades+"\n");
			quantidadeTweetsPorDia.forEach(
			    (dia, quantidade) -> tweet.append(dia + "=" + quantidade + "\n")
			);
			tweet.append("\n");
			
			//Retweets
			tweet.append("\n"+retweets +"\n");
			Map<String, Long> quantidadeRetweetsPorDia = atividadeFinalService.getQuantidadeRetweetsDiario();
			tweet.append(diasQuantidades+"\n");
			quantidadeRetweetsPorDia.forEach(
				(dia, quantidade) -> tweet.append(dia + "=" + quantidade + "\n")
			);	
			tweet.append("\n");
			
			//Mapeando as Favoritações
			tweet.append("\n"+favoritacoes+"\n");
			Map<String, Long> quantidadeFavoritesPorDia = atividadeFinalService.getQuantidadeFavoritacoes();
			tweet.append(diasQuantidades+"\n");
			quantidadeFavoritesPorDia.forEach(		   
				(dia, quantidade) -> tweet.append(dia + "=" + quantidade + "\n")
			);
			tweet.append("\n");

			//Primeiro e Último autor 
			tweet.append(primeiroAutor + atividadeFinalService.getPrimeiroAutor() + "\n");
			tweet.append(ultimoAutor+atividadeFinalService.getUltimoAutor() + "\n");
			tweet.append("\n");
			
			//Primeira e última data
			DateFormat df = new SimpleDateFormat("dd/MM");
			tweet.append(dataAntiga+df.format(atividadeFinalService.getDataRecente()) + "\n");
			tweet.append(dataRecente+df.format(atividadeFinalService.getDataAnterior()) + "\n");
			tweet.append("\n");
			
			tweet.append(endereco);
			
			postaTweet(tweet.toString());

		}
	}

	public static void postaTweet(String tweet) {
		try {
			Twitter twitter = AtividadeFinalConnection.getTwitter();
			Status status = twitter.updateStatus(tweet);
			
			System.out.println("Tweet postado com sucesso!");
			System.out.println();
			System.out.println(status.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
