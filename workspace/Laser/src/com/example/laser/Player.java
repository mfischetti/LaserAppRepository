package com.example.laser;

import java.io.Serializable;
import java.util.Hashtable;

@SuppressWarnings("serial")
public class Player implements Serializable {
	String playerName;
	int playerID;
	int gameID;
	int playerspot; // the spot in which they joined, ex. if they are the 5th player this would be 5
	int xLoc;
	int yLoc;
	int shotsFired;
	int hit;
	String team;
	int[] teamMembers;
	Hashtable<Integer, Integer> playerHit;
	Hashtable<Integer, Integer> hitByPlayer;
	
	public Player(int id, int gameID){
		this.playerID = id;
		this.gameID = gameID;
		this.shotsFired = 0;
		this.hit = 0;
		this.playerspot = 0;
		
		playerHit = new Hashtable<Integer, Integer>();
		hitByPlayer = new Hashtable<Integer, Integer>();
		for(int i = 1; i<=8; i++){
			playerHit.put(i, new Integer(0));
			hitByPlayer.put(i, new Integer(0));
		}
		
	}
	public void playerShoot(){
		//shoot laser beam
		shotsFired++;
	}
	public void setTeamMembers(int[] ids){
		for(int i = 0; i<=ids.length; i++){
			teamMembers[i]=ids[i];
		}
	}
	public void gotHit(int playerID){
		boolean teamMate = false;
		for(int i = 0; i<teamMembers.length; i++){
			if(teamMembers[i] == playerID){
				teamMate = true;
			}
			else{
				teamMate = false;
			}
		}
		if(teamMate == false){
			hit ++;
			Integer hits = (Integer) hitByPlayer.get(playerID);
			hits++;
			hitByPlayer.put(playerID, hits);
			//update mysql db by calling php with async function
		}
	}
	public void setPlayerSpot(int num){
		this.playerspot = num;
	}
	public void setTeam(String team){
		this.team = team;
	}
	public String getTeam(){
		return team;
	}
	public int getPlayerSpot(){
		return playerspot;
	}
	
	public int getPlayerID(){
		return playerID;
	}
	public int getGameID(){
		return gameID;
	}
	
	public void earseAll() {
		playerName = "";
		playerID = 0;
		gameID = 0;
		playerspot = 0;
		team = "";
	}
	
	
	
}
