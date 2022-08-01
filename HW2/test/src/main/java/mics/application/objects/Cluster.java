package mics.application.objects;


import mics.application.Events.DataPreProcessEvent;
import mics.application.Events.DataProcessedEvent;
import mics.application.services.CPUService;
import mics.application.services.GPUService;

import java.util.*;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private static Cluster instance;
	/**
     * Retrieves the single instance of this class.
     */
	private Map<CPUService, Queue<DataPreProcessEvent>> CPU_services;
	private Map<GPUService, Queue<DataProcessedEvent>> GPU_services;
	private Map<CPUService,Integer> ticksToFinish;

	private Cluster(){
		instance = null;
		CPU_services = new HashMap<>();
		GPU_services = new HashMap<>();
		ticksToFinish = new HashMap<>();
	}

	private int getCPU_speed(CPUService cpuService){
		return (32/cpuService.getCores());
	}

	public static Cluster getInstance() {
		//TODO: Implement this
		if(instance==null)instance=new Cluster();
		return instance;
	}
	public void registerGPU(GPUService gpuService){
		//System.out.println("Cluster::register("+gpuService.getName()+")");
		GPU_services.put(gpuService,new ArrayDeque<>());
	}
	public void registerCPU(CPUService cpuService){
		//System.out.println("Cluster::register("+cpuService.getName()+")");
		CPU_services.put(cpuService,new ArrayDeque<>());
		ticksToFinish.put(cpuService,0);
	}
	public synchronized void sendEvent(DataPreProcessEvent e){
		//System.out.println("Cluster::sendEvent DataPreProcess");
		CPUService cpu = findMin();
		CPU_services.get(cpu).add(e);
		int curr = ticksToFinish.get(cpu);
		curr+=e.getTypeMultiple()*getCPU_speed(cpu);
		ticksToFinish.replace(cpu,curr);
	}

	private synchronized CPUService findMin() {
		if(ticksToFinish.isEmpty()){
			System.out.println("ticks to finish is empty");
			return null;
		}
		CPUService min = null;
		int minTime=-1;
		for(CPUService cpu : ticksToFinish.keySet()){
			if(minTime==-1){
				min=cpu;
				minTime = ticksToFinish.get(cpu);
			}
			else{
				if(ticksToFinish.get(cpu)<minTime){
					min=cpu;
					minTime = ticksToFinish.get(cpu);
				}
			}
		}
		return min;
	}

	public void sendEvent(DataProcessedEvent e){
		//System.out.println("Cluster::sendEvent Processed");
		GPU_services.get(e.toSend()).add(e);
	}
	public  DataProcessedEvent awaitMessage(GPUService gpuService) {
		//System.out.println("Cluster::awaitMessage "+gpuService.getName());
		if(GPU_services.get(gpuService).isEmpty())return null;
		return GPU_services.get(gpuService).poll();
	}
	public DataPreProcessEvent awaitMessage(CPUService cpuService) {

		if(!CPU_services.containsKey(cpuService)){
			//System.out.println("CPU services doesnt contain key");
			return null;
		}
		if (CPU_services.get(cpuService).isEmpty())return null;
		DataPreProcessEvent res = CPU_services.get(cpuService).poll();
		if(res==null)return null;
		int ticks = res.getTypeMultiple()*getCPU_speed(cpuService);
		ProcessComplete(cpuService,ticks);
		return res;
	}

	private synchronized void ProcessComplete(CPUService cpuService, int ticks) {
		int curr =ticksToFinish.get(cpuService);
		curr=curr-ticks;
		if(curr<0) System.out.println("Curr<0 !!");
		ticksToFinish.replace(cpuService,curr);
	}

}
