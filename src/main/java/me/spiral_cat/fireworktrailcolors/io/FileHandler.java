package me.spiral_cat.fireworktrailcolors.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.spiral_cat.fireworktrailcolors.records.StateRecord;
import me.spiral_cat.fireworktrailcolors.records.TrailColorRecord;

public class FileHandler
{
	private static final String BASE_DIRECTORY_PATH = "plugins/FTC/";
	
	private FileHandler()
	{
		
	}
	
	public static StateRecord read()
	{

		try
		{
			String filePathString = String.format("%s.json", FileHandler.BASE_DIRECTORY_PATH);
			Path filePath = Paths.get(filePathString);
			String fileContent = Files.readString(filePath);
			return new ObjectMapper().readValue(fileContent, StateRecord.class);
		}
		catch (IOException e)
		{
			return new StateRecord();
		}
	}
	
	public static TrailColorRecord getPlayerTrailColor(UUID playerID)
	{
		StateRecord stateRecord = read();
		Map<UUID, TrailColorRecord> colorMap = stateRecord.getColorMap();
		if (colorMap.containsKey(playerID))
		{
			return colorMap.get(playerID);
		}
		else
		{
			return TrailColorRecord.getDefault();
		}
	}
	
	public static void write(UUID playerID, int red, int green, int blue, boolean on)
	{

		StateRecord state = FileHandler.read();
		Map<UUID, TrailColorRecord> colorMap = state.getColorMap();
		

		if (red == -1 || green == -1 || blue == -1)
		{
			red = getPlayerTrailColor(playerID).getRed();
			green = getPlayerTrailColor(playerID).getGreen();
			blue = getPlayerTrailColor(playerID).getBlue();
		}
		else
		{
			on = getPlayerTrailColor(playerID).getOn();
		}
		
		TrailColorRecord tc = new TrailColorRecord(red, green, blue, on);
		colorMap.put(playerID, tc);
		state.setColorMap(colorMap);
		
		try
		{
			Files.createDirectories(Paths.get(FileHandler.BASE_DIRECTORY_PATH));
			
			String filePathString = String.format("%s.json", FileHandler.BASE_DIRECTORY_PATH);
			Path filePath = Paths.get(filePathString);
			
			String jsonString = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(state);
			
			Files.writeString(filePath, jsonString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
