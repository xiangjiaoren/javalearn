package readcode;

//testThreadLocal1

public class testThreadLocal1 {
  //123
	
	 ThreadLocal<Long> longLocal = new ThreadLocal<Long>();
	    ThreadLocal<String> stringLocal = new ThreadLocal<String>();
	 
	    
/*	    private static final ThreadLocal<Long> longLocal = new ThreadLocal<Long>(){
	        *//**
	         * ThreadLocal没有被当前线程赋值时或当前线程刚调用remove方法后调用get方法，返回此方法值
	         *//*
	        @Override
	        protected Object initialValue()
	        {
	            System.out.println("调用get方法时，当前线程共享变量没有设置，调用initialValue获取默认值！");
	            return null;
	        }
	    };	    */
	    
	    
	    
	    public void set() {
	        longLocal.set(Thread.currentThread().getId());
	        stringLocal.set(Thread.currentThread().getName());
	    }
	     
	    public long getLong() {
	        return longLocal.get();
	    }
	     
	    public String getString() {
	        return stringLocal.get();
	    }
	     
	    public static void main(String[] args) throws InterruptedException {
	        final testThreadLocal1 test = new testThreadLocal1();
	        test.set();
	        System.out.println(test.getLong());
	        System.out.println(test.getString());
	 
	        Thread thread1 = new Thread(){
	            public void run() {
	                test.set();
	                System.out.println(test.getLong());
	                System.out.println(test.getString());
	            };
	        };
	        thread1.start();
	        thread1.join();
	         
	        System.out.println(test.getLong());
	        System.out.println(test.getString());
	    }
	
}
