/** 
* Copyright 2013 AppDynamics 
* 
* Licensed under the Apache License, Version 2.0 (the License);
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an AS IS BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.appdynamics.extensions.couchdb;

import java.util.HashMap;
import java.util.Iterator;

public class TestClass {

    public static void main(String[] args) {
        HashMap<String, HashMap<String, Number>> oldValues = new HashMap<String, HashMap<String, Number>>();
        HashMap<String, HashMap<String, Number>> newValues = new HashMap<String, HashMap<String, Number>>();

        oldValues.put("metricCategory1", generateOldTestValues());
        newValues.put("metricCategory1", generateNewTestValues());
        oldValues.put("metricCategory2", generateOldTestValues());
        newValues.put("metricCategory2", generateNewTestValues());
        oldValues.put("metricCategory3", generateOldTestValues());
        newValues.put("metricCategory3", generateNewTestValues());

        HashMap updatedValues = calculateCurrentMetrics(oldValues, newValues);
        printHashMap(updatedValues);
    }
    private static HashMap<String, Number> generateOldTestValues() {
        HashMap<String, Number> testValues = new HashMap<String, Number>();
        for (int i = 0; i < 10; i++) {
            testValues.put("Metric" + (i+1), 252.0);
        }
        return testValues;
    }
    private static HashMap<String, Number> generateNewTestValues() {
        HashMap<String, Number> testValues = new HashMap<String, Number>();
        for (int i = 0; i < 11; i++) {
            testValues.put("Metric" + (i+1), 300.0);
        }
        return testValues;
    }


    private static HashMap calculateCurrentMetrics(HashMap oldValues, HashMap newValues) {
        // Essentially want to subtract. i.e. newValues - oldValues = actual values in the interval
        HashMap<String, HashMap<String, Number>> currentMetrics = new HashMap<String, HashMap<String, Number>>();
        Iterator newValuesIterator = oldValues.keySet().iterator();

        while (newValuesIterator.hasNext()) {
            String metricCategory = (String) newValuesIterator.next();
            if (oldValues.containsKey(metricCategory)) {
                HashMap oldMetricMap = (HashMap) oldValues.get(metricCategory);
                HashMap newMetricMap = (HashMap) newValues.get(metricCategory);
                HashMap currentMetricMap = new HashMap<String, Number>();
                Iterator newMetricMapIterator = newMetricMap.keySet().iterator();
                currentMetrics.put(metricCategory, currentMetricMap);

                while (newMetricMapIterator.hasNext()) {
                    String metricName = (String) newMetricMapIterator.next();
                    Long newMetricValue = ((Number) newMetricMap.get(metricName)).longValue();
                    if (oldMetricMap.containsKey(metricName)) { // Need to subtract in order to get current values
                        Long oldMetricValue = ((Number) oldMetricMap.get(metricName)).longValue();
                        if (newMetricValue - oldMetricValue > 0) {
                            currentMetricMap.put(metricName, newMetricValue - oldMetricValue);
                        }
                        else {
                            currentMetricMap.put(metricName, newMetricValue);
                        }
                    }
                    else { // this is a new metric that is not present in the old metrics map
                        currentMetricMap.put(metricName, newMetricValue);
                    }
                }
            }
        }
        return currentMetrics;
    }

    private static void printHashMap(HashMap<String, HashMap<String, Number>> updatedValues) {
        Iterator outerIterator = updatedValues.keySet().iterator();
        while (outerIterator.hasNext()) {
            String outerKey = (String) outerIterator.next();
            HashMap innerHashMap = updatedValues.get(outerKey);
            Iterator innerIterator = innerHashMap.keySet().iterator();
            System.out.println("--Metric Category: " + outerKey);
            while (innerIterator.hasNext()) {
                String innerKey = (String) innerIterator.next();

                System.out.println("------MetricName: " + innerKey + "  MetricValue: " + innerHashMap.get(innerKey));
            }
        }
    }
}
