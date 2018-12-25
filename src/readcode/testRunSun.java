package readcode;


public class testRunSun extends testRunFather implements sun1 {

	void Method(String c) {
		System.out.println("this is son start" + c);
		// super.Method(c);
		System.out.println("this is son  end" + c);
		String wang2 = "123";
		System.out.println();
	}

	
	
	/* (non-Javadoc)
	 * @see readcode.sun1#toString()
	 */
	
	public String toString1() {
		return "testRunSun [getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

	@Override
	void Methodfather1() {
		// TODO Auto-generated method stub
		super.Methodfather1();

		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		method2();
	}

	private void method2() {
		System.out.println("asdfas");
	}

	public String toString3() {
		return null;
	}

}
