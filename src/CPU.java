//Asad Arif - s0937323
import java.io.*;
import java.util.*;
 
public class CPU
{
    private static String jobList = "D:/ListOfJobs.txt";
    private static ArrayList<Job> jobQueue = null;
    public static ArrayList<Job> completedJobs = new ArrayList<Job>();
    private static BufferedReader fileReader = null;
    private static String line = "";
    
    private static int memoryNeeded;
    private static final int TOTALMEMORY = 1000;
    
    public static boolean[] chunkStatus = new boolean[10];
    private static int occupiedChunks;
    private static final int TOTALCHUNKS = 10;

    
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
        
        if(memoryNeeded > TOTALMEMORY)
        {
            System.out.println("System does not have enough memory to complete job.");
            System.out.println("Job will be removed.");
            jobQueue.remove(0);
            jobQueue.trimToSize();
            loadJob();
        }
        else if(memoryNeeded <= TOTALMEMORY)
        {
       
            int chunksNeeded = memoryNeeded / 100;
            if (memoryNeeded % 100 != 0)
            	chunksNeeded += 1;
            
            updateChunkStatus();
            if(chunksNeeded <= TOTALCHUNKS - occupiedChunks)
            {
               System.out.println("New job added to memory.");
               Memory.loadPages(currentJob, chunksNeeded);
               jobQueue.remove(0);
               jobQueue.trimToSize();
            }
            else
            {
                System.out.println("Not enough memory available currently. Switching to next job");
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
      
    //Updates how many chunks are free by looping through the entire boolean array
    public static void updateChunkStatus()
    {
        for(int i = 0; i < chunkStatus.length; i++)
        {
            if (chunkStatus[i] == true)
            {
                occupiedChunks += 1;
            }
            else
            {
                occupiedChunks -= 1;
            }
        }
    }
    
    //Prints out what chunks are occupied with what jobs
    public static void getChunkStatus()
    {
    	updateChunkStatus();
        for(int i = 0; i < TOTALCHUNKS; i++)
        {
            if(chunkStatus[i] == true)
            {
                System.out.println("Chunk " + i + " is busy with Job: " + Memory.mainMemory[i].getJobID());
            }
            else
            {
                System.out.println("Chunk " + i + " is not busy.");
            }
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