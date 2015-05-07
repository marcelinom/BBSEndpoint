package com.brazilboatshare.model.entity;

import java.io.Serializable;

import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.googlecode.objectify.annotation.Embed;

@Embed
public class Local implements Serializable {
 
	private static final double raioTerra = 6371;	// Raio da Terra em km
	private static final long serialVersionUID = 6475743703054159593L;

	private String descricao;
	private double lat;
	private double lng;
	
	public Local() {}
	
	public Local(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}
	
	public Local(String descricao, double lat, double lng) {
		super();
		this.descricao = descricao;
		this.lat = lat;
		this.lng = lng;
	}

	public Local(GeoPoint point) {
		this.lat = point.getLatitude();
		this.lng = point.getLongitude();
	}
	
	static public double distancia(Local a, Local b) {
		//usa formula de Haversine
		double dLat = Math.toRadians(a.getLat() - b.getLat());
		double dLon = Math.toRadians(a.getLng() - b.getLng());
		double latA = Math.toRadians(a.getLat());
		double latB = Math.toRadians(b.getLat());
		double d = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(latA) * Math.cos(latB) * Math.sin(dLon/2) * Math.sin(dLon/2); 
		double c = 2 * Math.atan2(Math.sqrt(d), Math.sqrt(1-d)); 
		return raioTerra * c; // distancia em km. Em linha reta, obviamente...
	}
	
	public Local alvo(double distancia, double angulo) {
		// Destination point given distance (in km) and bearing (in degrees) from start point
		double brng = Math.toRadians(angulo); 		// bearing, in radians
		double distAng = distancia/raioTerra; 		// angular distance, in radians
		double lat1 = Math.toRadians(this.getLat());
		double lng1 = Math.toRadians(this.getLng());
		
		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(distAng) + Math.cos(lat1)*Math.sin(distAng)*Math.cos(brng) );
		double lng2 = lng1 + Math.atan2(Math.sin(brng)*Math.sin(distAng)*Math.cos(lat1), Math.cos(distAng)-Math.sin(lat1)*Math.sin(lat2));
		return new Local(Math.toDegrees(lat2), Math.toDegrees(lng2));
	}
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getDescricao() {
		return descricao==null?"":descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	// transformar em string JSON. Construtor JSONObject(Object obj) transforma inadequadamente...
	public JSONObject toJSON() throws JSONException {
		JSONObject jobj = new JSONObject();
		jobj.put("lat", lat);
		jobj.put("lng", lng);
		jobj.put("descricao",descricao);
		return jobj;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Local))
			return false;
		Local other = (Local) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Local [descricao=" + descricao + ", lat=" + lat + ", lng="
				+ lng + "]";
	}
	
}
 
