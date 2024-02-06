package com.MirukuStudio.MCJEPlayerAdvancementsMerge;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonParseException;

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

public class Main {
    private static final String TOOL_VERSION = "1.0-1.20.4";
    private static final String DATA_VERSION_KEY = "DataVersion";
    private static final String CATEGORY_CRITERIA_KEY = "criteria";
    private static final String CATEGORY_DONE_KEY = "done";
    private static final SimpleDateFormat STAMP_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    public static void main(String[] args) {
        args = new String[] {
                "-i", "1.json", "2.json", "3.json",
                "-o", "out.json",
                "-p"
        };

        Options options = new Options();

        Option optInputs = new Option("i", "inputs", true, "Target statistics JSON files");
        optInputs.setArgName("FILES");
        optInputs.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(optInputs);

        Option optOutput = new Option("o", "output", true, "Output statistics JSON file");
        optOutput.setArgName("FILE");
        options.addOption(optOutput);

        Option optPretty = new Option("p", "pretty", false, "Set output pretty printed [OPTIONAL]");
        options.addOption(optPretty);

        Option optHelp = new Option("h", "help", false, "Show help");
        options.addOption(optHelp);

        Option optVersion = new Option("v", "version", false, "Show version");
        options.addOption(optVersion);

        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(optHelp)) {
                HelpFormatter helpFormatter = new HelpFormatter();
                helpFormatter.printHelp("MCJE-PlayerStatsMerge.jar", options);
            } else if (cmd.hasOption(optVersion)) {
                System.out.println(TOOL_VERSION);
            } else {
                if (!cmd.hasOption(optInputs)) {
                    throw new ParseException("No input files!");
                } else {
                    if (cmd.getOptionValues(optInputs).length < 2) {
                        throw new ParseException("Not enough input files!");
                    }
                }
                if (!cmd.hasOption(optOutput)) {
                    throw new ParseException("No output file!");
                }

                String[] inputFilePaths = cmd.getOptionValues(optInputs);
                String outputFilePath = cmd.getOptionValue(optOutput);
                boolean isPretty = cmd.hasOption(optPretty);

                JsonObject jsonDataMg = new JsonObject();

                try {
                    for (String filePath: inputFilePaths) {
                        BufferedReader reader = new BufferedReader(new FileReader(filePath));

                        JsonElement jsonElement = JsonParser.parseReader(reader);
                        JsonObject jsonData = jsonElement.getAsJsonObject();

                        for (String childKey: jsonData.keySet()) {
                            if (childKey.equals(DATA_VERSION_KEY)) {
                                JsonPrimitive dataVersionPt = jsonData.getAsJsonPrimitive(DATA_VERSION_KEY);
                                int dataVersion = dataVersionPt.getAsInt();
                                if (!jsonDataMg.has(DATA_VERSION_KEY)) {
                                    jsonDataMg.add(DATA_VERSION_KEY, new JsonPrimitive(dataVersion));
                                } else {
                                    JsonPrimitive dataVersionMgPt = jsonDataMg.getAsJsonPrimitive(DATA_VERSION_KEY);
                                    int dataVersionMg = dataVersionMgPt.getAsInt();
                                    if (dataVersionMg < dataVersion) {
                                        jsonDataMg.add(DATA_VERSION_KEY, new JsonPrimitive(dataVersion));
                                    }
                                }
                            } else {
                                JsonObject category = jsonData.getAsJsonObject(childKey);
                                if (!jsonDataMg.has(childKey)) {
                                    jsonDataMg.add(childKey, new JsonObject());
                                }
                                JsonObject categoryMg = jsonDataMg.getAsJsonObject(childKey);

                                JsonObject criteria = category.getAsJsonObject(CATEGORY_CRITERIA_KEY);
                                if (!categoryMg.has(CATEGORY_CRITERIA_KEY)) {
                                    categoryMg.add(CATEGORY_CRITERIA_KEY, new JsonObject());
                                }
                                JsonObject criteriaMg = categoryMg.getAsJsonObject(CATEGORY_CRITERIA_KEY);

                                for (String stampKey: criteria.keySet()) {
                                    JsonPrimitive stampPt = criteria.getAsJsonPrimitive(stampKey);
                                    String stamp = stampPt.getAsString();

                                    if (!criteriaMg.has(stamp)) {
                                        criteriaMg.add(stampKey, new JsonPrimitive(stamp));
                                    } else {
                                        JsonPrimitive stampMgPt = criteriaMg.getAsJsonPrimitive(stampKey);
                                        String stampMg = stampMgPt.getAsString();
                                        Date date = STAMP_DATE_FORMAT.parse(stamp);
                                        long dateTime = date.getTime();

                                        Date dateMg = STAMP_DATE_FORMAT.parse(stampMg);
                                        long dateMgTime = dateMg.getTime();

                                        if (dateTime < dateMgTime) {
                                            criteriaMg.add(stampKey, new JsonPrimitive(stamp));
                                        }
                                    }
                                }

                                JsonPrimitive donePt = category.getAsJsonPrimitive(CATEGORY_DONE_KEY);
                                boolean done = donePt.getAsBoolean();

                                if (!categoryMg.has(CATEGORY_DONE_KEY)) {
                                    categoryMg.add(CATEGORY_DONE_KEY, new JsonPrimitive(done));
                                } else {
                                    JsonPrimitive doneMgPt = categoryMg.getAsJsonPrimitive(CATEGORY_DONE_KEY);
                                    boolean doneMg = doneMgPt.getAsBoolean();
                                    if (!doneMg && done) {
                                        categoryMg.add(CATEGORY_DONE_KEY, new JsonPrimitive(true));
                                    }
                                }
                            }
                        }

                        JsonPrimitive dataVersionMg = jsonDataMg.getAsJsonPrimitive(DATA_VERSION_KEY);
                        jsonDataMg.remove(DATA_VERSION_KEY);
                        jsonDataMg.add(DATA_VERSION_KEY, new JsonPrimitive(dataVersionMg.getAsInt()));
                    }

                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    if (isPretty) {
                        gsonBuilder.setPrettyPrinting();
                    }
                    Gson gson = gsonBuilder.create();

                    String mergedStatsJson = gson.toJson(jsonDataMg);
                    writer.write(mergedStatsJson);
                    writer.flush();
                    writer.close();
                } catch (IOException | JsonParseException | java.text.ParseException ex) {
                    System.out.println(ex.getClass().getName() + ": " + ex.getMessage());
                }
            }
        } catch (ParseException ex) {
            System.out.println("Argument Error: " + ex.getMessage());
        }
    }
}
