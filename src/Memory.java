//Asad Arif - s0937323
public class Memory
{
	
	public static Job[] mainMemory = new Job[10];
   
	//Fill up chunks with the amount of pages needed for the Job
	//I have no idea how to implement timers/threads so it only does 1 at a time
    public static void loadPages(Job job, int chunks)
    {
        try
        {
            for(int i = 0; i < mainMemory.length; i ++) 
            {
                if((CPU.chunkStatus[i] == false) && chunks > 0) 
                {
                    CPU.chunkStatus[i] = true;
                    mainMemory[i] = job;
                    chunks -= 1;
                }
            }
            
            CPU.getChunkStatus();
            
            //If runtime entered is not in milliseconds, convert to milliseconds because Thread.sleep() only uses milliseconds
            if(job.getRunTime() < 1000)
            	Thread.sleep(job.getRunTime() * 1000);
            else
            	Thread.sleep(job.getRunTime());
            
            for(int i = 0; i < mainMemory.length; i ++) 
            {
                if(mainMemory[i] == job) 
                {
                    CPU.chunkStatus[i] = false;
                    mainMemory[i] = null;
                    chunks -= 1;
                }
            }
        }
        catch(Exception e)
        {
        	System.out.println("Something went wrong");
        }
    }
}