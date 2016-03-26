package Model;

import java.io.Serializable;

public class LanguageAnalytics implements Serializable
{
	private String language;
	private Long countLanguage;

	public  static String resolveLanguage (String language) {
    	if (language.equals("ar")) {
    		return "Arabic";
    	} else if (language.equals("zh")) {
    		return "Chinese";
    	} else if (language.equals("nl")) {
    		return "Dutch";
    	} else if (language.equals("en")) {
    		return "English";
    	} else if (language.equals("fa")) {
    		return "Farsi";
    	} else if (language.equals("fi")) {
    		return "Finnish";
    	} else if (language.equals("fr")) {
    		return "French";
    	} else if (language.equals("hi")) {
    		return "Hindi";
    	} else if (language.equals("id")) {
    		return "Indonesian";
    	} else if (language.equals("it")) {
    		return "Italian";
    	} else if (language.equals("ja")) {
    		return "Japanese";
    	} else if (language.equals("ko")) {
    		return "Korean";
    	} else if (language.equals("no")) {
    		return "Norwegian";
    	} else if (language.equals("pl")) {
    		return "Polish";
    	} else if (language.equals("pt")) {
    		return "Portuguese";
    	} else if (language.equals("ru")) {
    		return "Russian";
    	} else if (language.equals("es")) {
    		return "Spanish";
    	} else if (language.equals("sv")) {
    		return "Swedish";
    	} else if (language.equals("th")) {
    		return "Thai";
    	} else if (language.equals("tr")) {
    		return "Turkish";
    	}
    	else return "Unknown"; 	
    }

	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public long getCountLanguage() {
		return countLanguage;
	}
	public void setCountLanguage(long countLanguage) {
		this.countLanguage = countLanguage;
	}
}
