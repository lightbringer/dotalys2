package de.lighti.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import de.lighti.model.game.PositionDouble;
import de.lighti.model.game.PositionInteger;

public class Centroid  implements Cloneable {
	double xSum;
	double ySum;
	int memberCount;
	 @Id
	 @GeneratedValue( strategy = GenerationType.TABLE )
	 private long id;
	public Centroid(int x, int y, int c){
		xSum = x; ySum =y; memberCount = c;				
	}
	
	public void addPoint(int x, int y){
		xSum+=x; ySum+=y;
		memberCount++;
	}
	
	public void removePoint(int x, int y) throws Exception{
		if(memberCount == 1)
			throw new Exception("Removal would lead to an empty centroid!");
		xSum-=x; ySum-=y;
		memberCount--;		
	}
	
	public PositionDouble getPosition(){
		return new PositionDouble(xSum/memberCount, ySum/memberCount);
	}
	
	public double getX(){
		return xSum/memberCount;
	}

	public double getY(){
		return ySum/memberCount;
	}
	

    @Override
    public Object clone() {
        try {
            final Centroid clone = (Centroid) super.clone();
            return clone;
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException( e );
        }
    }
}
