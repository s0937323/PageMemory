//Asad Arif - s0937323
import java.io.*;
import java.util.*;
 
public class CPU
{
    private static String jobList = "D:/ListOfJobs.txt";
    private static ArrayList<Job> jobQueue = null;
    private static BufferedReader fileReader = null;
    private static String line = "";
    
    private static int memoryNeeded;
   // private static final int TOTALMEMORY = 1000;
    
    public static boolean[] partitionStatus = new boolean[5];
    //200 + 300 + 200 + 100 + 200
    private static int occupiedPartitions;
    private static final int TOTALPARTITIONS = 5;
    
    private static final int PART1 = 200;
    private static final int PART2 = 300;
    private static final int PART3 = 200;
    private static final int PART4 = 100;
    private static final int PART5 = 200;
    public static Job[] partitionsFilled = new Job[5];
    private static final int BIGGESTPARTITION = PART2;
    public static Partition p = new Partition();
	public static Thread part1 = new Thread(p);
	public static Thread part2 = new Thread(p);
	public static Thread part3 = new Thread(p);
	public static Thread part4 = new Thread(p);
	public static Thread part5 = new Thread(p);

    
    //Read data from text file and populate jobQueue with contents
    public static void readFromFile()
    {
        try
        {
            jobQueue = new ArrayList<Job>();
            jobQueue.ensureCapacity(5);
            fileReader = new BufferedReader(new FileReader(jobList));
            while ((line = fileReader.readLine()) != null)
            {
                String[] values = line.split(",", 3);
                jobQueue.add(new Job(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])));
            }             
            
            updateChunkStatus();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Cannot find file.");
        }
        catch (IOException e)
        {
            System.out.println("Cannot read file.");
        }
        finally
        {
            if (fileReader != null)
            {
                try
                {
                    fileReader.close();
                }
                catch (IOException e)
                {
                	System.out.println("Something went wrong.");
                }
            }
        }
    }
   

    //Will load 1 job at a time from the Queue and check to see if it can be done with available memory
    //Jobs will never be moved to the back of the queue because I can only run 1 job at a time (didn't figure out how to do multiple at once)
    public static void loadJob()
    {  
    	
        Job currentJob = jobQueue.get(0);
        memoryNeeded = currentJob.getMemoryNeeded();
        
        if(memoryNeeded > BIGGESTPARTITION)
        {
            System.out.println("System partitions do not have enough memory to complete job.");
            System.out.println("Job will be removed.");
            jobQueue.remove(0);
            jobQueue.trimToSize();
            loadJob();
        }
        else if(memoryNeeded <= BIGGESTPARTITION)
        {          
           // updateChunkStatus();
            if(checkPartitions(currentJob.getMemoryNeeded()) >= 0)
            {
               System.out.println("New job added to memory.");
               Memory.loadPages(currentJob, checkPartitions(currentJob.getMemoryNeeded()));
               jobQueue.remove(0);
               jobQueue.trimToSize();
            }
            else
            {
            	System.out.println("All partitions filled. Moving Job to back of the queue.");
                jobQueue.remove(0);
                jobQueue.add(currentJob);
                jobQueue.trimToSize();
            }
        }
        else
        {
            loadJob();
        }
    }
      
    private static int checkPartitions(int memory)
    {
			if(partitionsFilled[0] == null)
			{
				if(memory <= PART1)
				{
					return 0;
				}
			}
			
			if(partitionsFilled[1] == null)
			{
				if(memory <= PART2)
				{
					return 1;
				}
			}
			
			if(partitionsFilled[2] == null)
			{
				if(memory <= PART3)
				{
					return 2;
				}
			}
			
			if(partitionsFilled[3] == null)
			{
				if(memory <= PART4)
				{
					return 3;
				}
			}
			
			if(partitionsFilled[4] == null)
			{
				if(memory <= PART5)
				{
					return 4;
				}
			}
			
			return -1;
		}


	//Updates how many chunks are free by looping through the entire boolean array
    public static void updateChunkStatus()
    {
        for(int i = 0; i < partitionStatus.length; i++)
        {
            if (partitionStatus[i] == true)
            {
                occupiedPartitions += 1;
            }
            else
            {
                occupiedPartitions -= 1;
            }
        }
    }
    
    //Prints out what chunks are occupied with what jobs
    public static void getChunkStatus()
    {
    	updateChunkStatus();
        for(int i = 0; i < TOTALPARTITIONS; i++)
        {
            if(partitionStatus[i] == true)
            {
                System.out.println("Chunk " + i + " is busy with Job: " + Memory.mainMemory[i].getJobID());
            }
            else
            {
                System.out.println("Chunk " + i + " is not busy.");
            }
        }	
    }
    
    public static void startThread(Job job, int partition)
    {
    	if(partition == 0)
    	{
    		partitionsFilled[0] = job;
    		part1.start();
    	}
    	if(partition == 1)
    	{
    		partitionsFilled[1] = job;
    		part2.start();
    	}
    	if(partition == 2)
    	{
    		partitionsFilled[2] = job;
    		part3.start();
    	}
    	if(partition == 3)
    	{
    		partitionsFilled[3] = job;
    		part4.start();
    	}
    	if(partition == 4)
    	{
    		partitionsFilled[4] = job;
    		part5.start();
    	}
    }
   
    //Main method
    public static void main(String[] args)
    {
        readFromFile();
        
        while(jobQueue.isEmpty() == false)
        {
        	loadJob();
        }
        
        //I have no idea what is causing the program to crash once everything is done, but it complete all jobs
        System.out.println("-------------------------------------------");
        System.out.println("System has completed all jobs!");
    }
}