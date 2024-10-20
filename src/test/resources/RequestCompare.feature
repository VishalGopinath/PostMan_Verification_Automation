Feature: JSON Comparison

  Scenario: Compare JSON files from two folders and save the results
    Given I have JSON files in "D:/Request_Automation/3. Request Comparation/1. Response/Folder 1/"
    And I have corresponding JSON files in "D:/Request_Automation/3. Request Comparation/1. Response/Folder 2/"
    When I compare the files
    Then I should save the comparison results in "D:/Request_Automation/3. Request Comparation/2. JsonDifferences"
