package com.brazilboatshare.util;

public enum Moeda {
	REAL {
		@Override
		public String toString() {
			return "Real";
		}

		@Override
		public String simbolo() {
			return "R$";
		}

		@Override
		public String codigo() {
			return "BRL";
		}
	},
	DOLAR {
		@Override
		public String toString() {
			return "Dólar";
		}

		@Override
		public String simbolo() {
			return "US$";
		}

		@Override
		public String codigo() {
			return "USD";
		}
	};

	@Override
	public String toString() {
		return null;
	}

	public String simbolo() {
		return null;
	}

	public String codigo() {
		return null;
	} // codigo de tres letras, cfe. ISO 4217

}
