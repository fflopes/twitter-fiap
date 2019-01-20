package br.com.fiap.atividadefinal.connection;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class AtividadeFinalConnection {
	
	  
	
	 /**
		 * Dados do Usuario do Twiiter
		 * 
		 * 
		 */
		private final static String apiKey = "QHP9dSte1LUX2tKWuJSdVTpD8";
		private final static String apiSecretKey = "gozGfqmzGvx1vDEDCE2W0hkmDpMLjkHnpKxecgZ2AfMaa4WHS5";
		private final static String accessToken = "181736274-vXppPaY1EkdNRK6oE7uYYq8DDSqFQGF7Qr1e7ryf";
		private final static String accessTokenSecret = "pRbCezqqKPDoOvPkZhltD6MBsAwQEuAwMpDayPcWXQTcq";

		
		 /**
		 * 
		 * @return Configuracões no objeto
		 */
		private static Twitter twitter;
		
		public static Twitter getTwitter() {
			if (twitter == null) {
				AccessToken accessToken = loadAccessToken();
				twitter = TwitterFactory.getSingleton();
				twitter.setOAuthConsumer(apiKey, apiSecretKey);
				twitter.setOAuthAccessToken(accessToken);
			}
			return twitter;
		}

		
		/**
		 * Acesso com os Dados do Usuario Twitter
		 * 
		 */
		private static AccessToken loadAccessToken(){
			return new AccessToken(accessToken, accessTokenSecret);
		}

}
