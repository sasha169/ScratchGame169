package org.scratchGame.mavenScratchGame;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {

	public static void toReadAndWriteData() throws IOException, ParseException, JsonProcessingException, JsonMappingException,
			NoSuchFieldException, SecurityException {

		JSONParser jsonParser = new JSONParser();
		FileReader reader = new FileReader(".\\jsonfiles\\config.json");
		Object obj = jsonParser.parse(reader);
		JSONObject configJsonObj = (JSONObject) obj;

		// to fetch rows and column
		Long columnsValue = (Long) configJsonObj.get("columns");
		Long rowValue = (Long) configJsonObj.get("rows");
        String[][] arrayString = generateArray(rowValue,columnsValue);
				
        String[] result = Arrays.stream(arrayString).flatMap(Arrays::stream).toArray(String[]::new);

		Map<String, Long> countOccurance = Arrays.stream(result)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		boolean winOrLoss = false;
		int count = 0;
		Map<String, Long> repeatedValues = new HashMap<String, Long>();
		for (Map.Entry<String, Long> entry : countOccurance.entrySet()) {
			if (entry.getValue() >= 3) {
				count++;
				repeatedValues.put(entry.getKey(), entry.getValue());
			} else {
				winOrLoss = false;
			}
		}
		List<String> bonusSymbolList = Arrays.asList("10x", "5x", "+1000", "+500");
		List<String> inputAsString = Arrays.stream(arrayString).flatMap(Arrays::stream).collect(Collectors.toList());
		String[] appliedBonusSymbol = inputAsString.stream().filter(bonusSymbolList::contains)
				.toArray(String[]::new);
		ObjectMapper mapper=new ObjectMapper();
		 WonGame wg=new WonGame();
		 wg.setMatrix(arrayString);
		long amount=0;
		if (count >= 1) {
			Scanner sc = new Scanner(System.in);
			System.out.println("bet_amount:");
			long bettingAmount = sc.nextLong();
			amount = toGetWinningCalculationAmount(configJsonObj, arrayString, repeatedValues, bettingAmount);
			wg.setReward(amount);
			wg.setAppliedBonusSymbol(appliedBonusSymbol);
			String jsonData = mapper.writeValueAsString(wg);
			System.out.println("![Won Game](won_game_3x3.png)Won");
			System.out.println("jsonData::" + jsonData);

		} else {
         System.out.println("![Lost Game](lost_game.png) Lost");
         String jsonData = mapper.writeValueAsString(wg);
         System.out.println("jsonData::" + jsonData);
		}
		
				
	}
	
	public static long toGetRewardMultiplierSpecificSymbol(JSONObject configJsonObj, String symbolValue) {
		JSONObject json = (JSONObject) configJsonObj.get("symbols");
		JSONObject value = (JSONObject) json.get(symbolValue);
		long rewardMultiplier = 0;
		String type = (String) value.get("type");
		
		if (type.equals("standard")) {
			rewardMultiplier = (long) value.get("reward_multiplier");
		} 
		return rewardMultiplier;
	}

	public static String toGetSameSymbolWinCombination(JSONObject configJsonObj, long count) {
		JSONObject json = (JSONObject) configJsonObj.get("win_combinations");
		int countValue = (int) count;
		JSONObject winningCount = (JSONObject) json.get("same_symbol_" + countValue + "_times");
		String value = winningCount.get("reward_multiplier").toString();
        return value;
	}

	public static long toGetHorizontalSameSymbolWinCombination(JSONObject configJsonObj, String[][] array, String key) {
		long rewardMultiplier = 1;
		boolean flag = false;
		JSONObject json = (JSONObject) configJsonObj.get("win_combinations");
		JSONObject sameSymbol = (JSONObject) json.get("same_symbols_horizontally");
		for (int i = 0; i < array.length; i++) {
			for (int j = 1; j < array.length - 1; j++) {
				if (array[i][j - 1].equals(key) && array[i][j].equals(key) && array[i][j + 1].equals(key)) {
					flag = true;
					rewardMultiplier = (long) sameSymbol.get("reward_multiplier");
				}

			}
		}
		
		return rewardMultiplier;
	}

	public static long toGetVerticalSameSymbolWinCombination(JSONObject configJsonObj, String[][] array, String key) {
		long rewardMultiplier = 1;
		// boolean flag=false;
		JSONObject json = (JSONObject) configJsonObj.get("win_combinations");
		JSONObject sameSymbol = (JSONObject) json.get("same_symbols_horizontally");
		for (int i = 0; i < array.length; i++) {
			if (generateVertical(array, i, key) == true)
				rewardMultiplier = (long) sameSymbol.get("reward_multiplier");
			
		}
		return rewardMultiplier;
	}

	public static boolean generateVertical(String[][] array, int value, String key) {
		boolean result = false;
		for (int i = 0; i < array.length; i++) {
			int j = value;
			if (array[i][j].equals(key))
				;
			result = true;
		}

		return result;
	}

	public static long toGetDiagonallyRightToLeftSameSymbolWinCombination(JSONObject configJsonObj, String[][] array,
			String key) {
		long rewardMultiplier = 1;
		JSONObject json = (JSONObject) configJsonObj.get("win_combinations");
		JSONObject sameSymbol = (JSONObject) json.get("same_symbols_diagonally_right_to_left");
		if (generateDiagnolRightToLeft(array, 2, key) == true)
			rewardMultiplier = (long) sameSymbol.get("reward_multiplier");
		return rewardMultiplier;
	}

	public static boolean generateDiagnol(String[][] array, String key) {
		boolean result = false;
		for (int i = 0; i < array.length; i++) {
			if (array[0][0].equals(key) && array[1][1].equals(key) && array[2][2].equals(key)) {
				result = true;
			}
		}
		return result;
	}

	public static boolean generateDiagnolRightToLeft(String[][] array, int value, String key) {
		boolean result = false;
		int j = value;
		for (int i = 0; i < array.length; i++) {
			if (key.equals(array[0][2]) && key.equals(array[1][1]) && key.equals(array[2][0])) {
				result = true;
				j--;
			}
		}

		return result;
	}

	public static long toGetDiagonallyLeftToRightSameSymbolWinCombination(JSONObject configJsonObj, String[][] array,
			String key) {
		long rewardMultiplier = 1;
		JSONObject json = (JSONObject) configJsonObj.get("win_combinations");
		JSONObject sameSymbol = (JSONObject) json.get("same_symbols_diagonally_left_to_right");
		if (generateDiagnol(array, key) == true)
			rewardMultiplier = (long) sameSymbol.get("reward_multiplier");
		return rewardMultiplier;

	}

	public static String[][] generateArray(long rowValue,long columnsValue) {
		String arr[][] = new String[(int) rowValue][(int) columnsValue];
		Random randNum = new Random();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int x = randNum.nextInt(11);
				switch (x) {
				case 0: {
					arr[i][j] = "A";
					break;
				}
				case 1: {
					arr[i][j] = "B";
					break;
				}
				case 2: {
					arr[i][j] = "C";
					break;
				}
				case 3: {
					arr[i][j] = "D";
					break;
				}
				case 4: {
					arr[i][j] = "E";
					break;
				}
				case 5: {
					arr[i][j] = "F";
					break;
				}
				case 6: {
					arr[i][j] = "10x";
					break;
				}
				case 7: {
					arr[i][j] = "5x";
					break;
				}
				case 8: {
					arr[i][j] = "+1000";
					break;
				}
				case 9: {
					arr[i][j] = "+500";
					break;
				}
				default: {
					arr[i][j] = "MISS";
					break;
				}

				}
			}
		}

		return arr;

	}

	public static void main(String[] args) throws IOException, ParseException, NoSuchFieldException, SecurityException {

		toReadAndWriteData();
		

	}

	public static double toGetBonusSymbolRewards(JSONObject configJsonObj, String[][] arrayString, double reward) {

		List<String> bonusSymbolList = Arrays.asList("10x", "5x", "+1000", "+500");

		List<String> inputAsString = Arrays.stream(arrayString).flatMap(Arrays::stream).collect(Collectors.toList());
		List<String> intersection = inputAsString.stream().filter(bonusSymbolList::contains)
				.collect(Collectors.toList());
		
		for (String str : intersection) {
			JSONObject json = (JSONObject) configJsonObj.get("symbols");
			JSONObject value = (JSONObject) json.get(str);
			long rewardMultiplier = 0;
			long extra = 0;
			String type = (String) value.get("type");
			String impact = (String) value.get("impact");
			if (type.equals("bonus")) {
				if (impact.equals("multiply_reward")) {
					rewardMultiplier = (long) value.get("reward_multiplier");
					reward = reward * rewardMultiplier;
				} else {
					extra = (long) value.get("extra");
					reward = reward + extra;
				}

			}
		}

		return reward;
	}

	public static double rewardCalculation(JSONObject configJsonObj, long sameSymbolWinCombination, String key,
			String[][] arrayString, long bettingAmount) {
		
		

		double rewardCalculated = bettingAmount * toGetRewardMultiplierSpecificSymbol(configJsonObj, key)
				* sameSymbolWinCombination * toGetHorizontalSameSymbolWinCombination(configJsonObj, arrayString, key)
				* toGetVerticalSameSymbolWinCombination(configJsonObj, arrayString, key)
				* toGetDiagonallyLeftToRightSameSymbolWinCombination(configJsonObj, arrayString, key)
				* toGetDiagonallyRightToLeftSameSymbolWinCombination(configJsonObj, arrayString, key);
		return rewardCalculated;

	}

	public static long toGetWinningCalculationAmount(JSONObject configJsonObj, String[][] arrayString,
			Map<String, Long> aMap, long bettingAmount) {

		double reward = 0;
		for (Map.Entry<String, Long> entry : aMap.entrySet()) {
			String s = toGetSameSymbolWinCombination(configJsonObj, entry.getValue());
			long sameSymbolWinCombination = Long.parseLong(s);
			if (aMap.size() < 2) {
				reward = rewardCalculation(configJsonObj, sameSymbolWinCombination, entry.getKey(), arrayString,
						bettingAmount);

			} else {
				reward = reward + rewardCalculation(configJsonObj, sameSymbolWinCombination, entry.getKey(),
						arrayString, bettingAmount);

			}
			

		}
		double totalReward = toGetBonusSymbolRewards(configJsonObj, arrayString, reward);
		return (long) totalReward;
	}

}
