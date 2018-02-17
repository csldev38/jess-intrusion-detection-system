/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MainJavaFX;


import javax.swing.SwingUtilities;

/**
 *
 * @author Kavish
 */
public abstract class ThreadHandler {
    
    private Object value;
    private Thread thread;
    
    private static class ThreadVar
    {
        private Thread thread;
        ThreadVar(Thread t)
        {
            thread = t;
        }
        
        synchronized Thread get()
        {
            return thread;
        }
        
        synchronized void clear()
        {
            thread = null;
        }
        
    }
    
    private ThreadVar threadVar;
    
    protected synchronized Object getValue()
    {
        return value;
    }
    
    private synchronized void setValue(Object x)
    {
        value = x;
    }
    
    public abstract Object construct();
    
    public void finished(){}
    
    public void interrupt()
    {
        Thread t = threadVar.get();
        
        if(t!=null)
        {
            t.interrupt();
        }
        threadVar.clear();
    }
    
    public Object get()
    {
        while(true)
        {
            Thread t = threadVar.get();
            if(t==null)
            {
                return getValue();
            }
            
            try
            {
                t.join();
            }
            catch(InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        return null;
                    }
        }
    }
    
    
    public ThreadHandler()
    {
        final Runnable doFinished = new Runnable()
        {
            public void run()
            {
                finished();
            }
        };
        
        Runnable doConstruct = new Runnable()
        {
            public void run()
            {
                try
                {
                    setValue(construct());
                }
                finally
                {
                    threadVar.clear();
                }
                
                SwingUtilities.invokeLater(doFinished);
            }
        };
        
        Thread t = new Thread(doConstruct);
        threadVar = new ThreadVar(t);
    }
    
    public void start()
    {
        Thread t = threadVar.get();
        
        if(t!=null)
        {
            t.start();
        }
    }
    
}
