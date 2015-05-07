package com.brazilboatshare.util;

public enum Idioma {
	PORTUGUES {
		@Override
		public String toString() {return "Português";}
		@Override
		public String codigo() {return "pt";}
	},
	ESPANHOL {
		@Override
		public String toString() {return "Español";}
		@Override
		public String codigo() {return "es";}
	},
	INGLES {
		@Override
		public String toString() {return "English";}
		@Override
		public String codigo() {return "en";}
	};
	
	@Override
	public String toString() {return null;}
	public String codigo() {return null;}
}
