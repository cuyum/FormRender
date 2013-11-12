package ar.com.cuyum.cnc.domain.jsonsla;


public interface Numero {
	public Boolean lessThanOrEqual(Numero other);
    public Numero sum(Numero value);
    public Numero multiply(Numero value);
    public Numero divide(Numero value);
}
