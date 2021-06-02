package model;

/**
 * A 2D vector math supply.
 * @author TRAFNY Theo - UPSSITECH
 *
 */
public class Vector {
	
	/** Vector's both component
	 */
	public float x, y;
	
	/**
	 * 
	 * @param x x component
	 * @param y y component
	 */
	public Vector(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Add vector's components with the related parameter
	 * @param x The added value to the x component
	 * @param y The added value to the y component
	 * @see me.twodengine.engine.elements.Vector2D#addX(float)
	 * @see me.twodengine.engine.elements.Vector2D#addY(float)
	 */
	public void add(float x, float y)
	{
		this.x += x;
		this.y += y;
	}
	
	public Vector _add(float x, float y)
	{
		return new Vector(this.x + x, this.y + y);
	}
	/**
	 * Add x to the x component
	 * @param x The value that will be used for the addition
	 * @see me.twodengine.engine.elements.Vector2D#add(float, float)
	 */
	public void addX(float x) { this.x += x; }
	/**
	 * Add y to the y component
	 * @param y The value that will be used for the addition
	 * @see me.twodengine.engine.elements.Vector2D#add(float, float)
	 */
	public void addY(float y) { this.y += y; }
	
	public void add(Vector other) { this.x += other.x; this.y += other.y; }
	public Vector _add(Vector other) { return this._add(other.x, other.y); }
	
	public void sub(float x, float y)
	{
		this.x -= x;
		this.y -= y;
	}
	
	public Vector _sub(float x, float y)
	{
		return new Vector(this.x - x, this.y - y);
	}
	
	public void sub(Vector other) { this.x -= other.x; this.y -= other.y; }
	public Vector _sub(Vector other) { return this._sub(other.x, other.y); }
	
	/**
	 * Increment the x component by 1
	 * @see me.twodengine.engine.elements.Vector2D#incY()
	 */
	public void incX() { this.x++; }
	/**
	 * Increment the y component by 1
	 * @see me.twodengine.engine.elements.Vector2D#incX()
	 */
	public void incY() { this.y++; }

	
	/**
	 * Decrement the x component by 1
	 * @see me.twodengine.engine.elements.Vector2D#decY()
	 */
	public void decX() { this.x--; }
	/**
	 * Decrement the y component by 1
	 * @see me.twodengine.engine.elements.Vector2D#decX()
	 */
	public void decY() { this.y--; }
	
	public void scale(float scale)
	{
		this.x *= scale;
		this.y *= scale;
	}
	
	public Vector _scale(float scale)
	{
		return new Vector(this.x * scale, this.y * scale);
	}
	
	public void invert()
	{
		this.x *= -1;
		this.y *= -1;
	}
	
	public Vector _invert()
	{
		return new Vector(this.x * -1, this.y * -1);
	}
	
	public void invertX()
	{
		this.x *= -1;
	}
	
	public void invertY()
	{
		this.y *= -1;
	}
	
	/**
	 * Return the x component
	 * @return x component
	 * @see me.twodengine.engine.elements.Vector2D#getY()
	 */
	public float getX() {
		return x;
	}
	/**
	 * Return the y component
	 * @return y component
	 * @see me.twodengine.engine.elements.Vector2D#getX()
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * Change the value of the x component
	 * @param x x component
	 * @see me.twodengine.engine.elements.Vector2D#setY(float)
	 */
	public void setX(float x) {
		this.x = x;
	}
	/**
	 * Change the value of the y component
	 * @param y y component
	 * @see me.twodengine.engine.elements.Vector2D#setX(float)
	 */
	public void setY(float y) {
		this.y = y;
	}
	
	public void set(Vector vector)
	{
		this.x = vector.x;
		this.y = vector.y;
	}
	
	/**
	 * Change vector's components with another vector's components
	 * @param other Another {@link me.twodengine.engine.elements.Vector2D}
	 * @see me.twodengine.engine.elements.Vector2D
	 * @see me.twodengine.engine.elements.Vector2D#setX(float)
	 * @see me.twodengine.engine.elements.Vector2D#setY(float)
	 */
	public void equalsTo(Vector other)
	{
		this.x = other.x;
		this.y = other.y;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Vector)) return false;
		Vector v = (Vector) o;
		return this.x == v.x && this.y == v.y; 
	}
	
	public float dist(Vector other)
	{
		return (float) Math.hypot(other.x - this.x, other.y - this.y);
	}
	
	public float mag()
	{
		return (float) Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	public Vector normalize()
	{
		return new Vector(this.x / this.mag(), this.y / this.mag());
	}
	
	public Vector to(Vector other)
	{
		return new Vector(other.x - this.x, other.y - this.y);
	}
	
	/**
	 * Return the vector with the lowest components
	 * @param other
	 * @return
	 */
	public Vector min(Vector other)
	{
		if(this.x < other.x || this.y < other.y) return this;
		else return other;
	}
	
	/**
	 * Return the vector with the greatest components
	 * @param other
	 * @return
	 */
	public Vector max(Vector other)
	{
		if(this.x > other.x || this.y > other.y) return this;
		else return other;
	}
	
	public float dot(Vector other)
	{
		return this.x * other.x + this.y * other.y;
	}
	
	public double angle(Vector other)
	{
		Vector normalized = this.normalize();
		Vector o_normalized = other.normalize();
		return Math.atan2(o_normalized.y, o_normalized.x) - Math.atan2(this.y, this.x); // This formula is used for relative angle (+ or -)
//		return Math.acos(normalized.dot(o_normalized));
	}
	
	public static Vector intersectionPoint(Vector p1, Vector dir1, Vector p2, Vector dir2)
    {
		Vector R = dir1.copy();
		Vector S = dir2.copy();
	    R.normalize();
	    S.normalize();

	    Vector QP  = p2._sub(p1);
	    Vector SNV = new Vector(S.y, -S.x);

	    float t  =  QP.dot(SNV) / R.dot(SNV); 

	    Vector X = p1._add(R._scale(t));
	    return X;
    }
	
	public Vector copy() { return new Vector(this.x, this.y); }

	/**
	 * Serialize vector's components to print it
	 */
	public String toString()
	{
		return this.x + "," + this.y;
	}

}
