import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class indivproj
{
	// Database credentials
    final static String HOSTNAME = "prio0002-sql-server.database.windows.net";
    final static String DBNAME = "cs-dsa-4513-sql-db";
    final static String USERNAME = "prio0002";
    final static String PASSWORD = "DBi2G0T3D#4512";


	// Database connection string
    final static String URL = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;",
            HOSTNAME, DBNAME, USERNAME, PASSWORD);
	
	// Query templates
    final static String QUERY_TEMPLATE_1 = "exec AddNewTeam @team_name = ?, @team_type = ?, @date = ?;";

    final static String QUERY_TEMPLATE_2 = "exec AddNewClient @client_SSN = ?, @name = ?, @gender = ?, @profession = ?, @is_on_mailing_list = ?,"
    		+ " @doctor_name = ?, @doctor_phone_number = ?, @first_assignment_date = ?, @shopping_needs = ?,"
    		+ " @visiting_needs = ?, @food_needs = ?, @yardwork_needs = ?, @housekeeping_needs = ?, "
    		+ "@transportation_needs = ?, @volunteer_SSN = ?, @team_name = ?, @status = ?, @emer_name = ?, "
    		+ "@emer_phone_number = ?, @emer_relationship = ?, @client_mailing_address = ?, @client_phone_number = ?, "
    		+ "@client_email_address = ?, @policy_id = ?, @provider_name = ?, @provider_address = ?, @insurance_type = ?;";
    final static String QUERY_TEMPLATE_3 = "exec AddNewVolunteer @volunteer_SSN = ?, @name = ?, @gender = ?, @profession = ?, @is_on_mailing_list = ?, @date_joined = ?, @recent_training_course_date = ?, @recent_training_course_location = ?, @team_name = ?, @emer_name = ?, @emer_phone_number = ?, @emer_relationship = ?, @vol_mailing_address = ?, @vol_phone_number = ?, @vol_email_address = ?;";
    final static String QUERY_TEMPLATE_4 = "exec UpdateVolunteerHours @SSN = ?, @team_name = ?, @hours = ?;";
    final static String QUERY_TEMPLATE_5 = "exec AddNewEmployee @employee_SSN = ?, @name = ?, @gender = ?, @profession = ?, @is_on_mailing_list = ?, @salary = ?, @marital_status = ?, @hire_date = ?, @volunteer_SSN = ?, @team_name = ?, @date = ?, @content = ?, @emer_name = ?, @emer_phone_number = ?, @emer_relationship = ?, @emp_mailing_address = ?, @emp_phone_number = ?, @emp_email_address = ?;";
    final static String QUERY_TEMPLATE_6 = "exec AddExpense @SSN = ?, @date = ?, @amount = ?, @description = ?;";
    final static String QUERY_TEMPLATE_7 = "exec AddNewDonor @don_SSN = ?, @name = ?, @gender = ?, @profession = ?, @is_on_mailing_list = ?, @status_of_anonymity = ?, @emer_name = ?, @emer_phone_number = ?, @emer_relationship = ?, @don_mailing_address = ?, @don_phone_number = ?, @don_email_address = ?;";
    final static String QUERY_TEMPLATE_8 = "exec GetClientDoctorInfo @SSN = ?;";
    final static String QUERY_TEMPLATE_9 = "exec GetEmployeeExpenses @start_date = ?, @end_date = ?;";
    final static String QUERY_TEMPLATE_10 = "exec GetVolunteersSupportingClient @client_SSN = ?;";
    final static String QUERY_TEMPLATE_11 = "exec GetTeamsFoundedAfter @date = ?;";
    final static String QUERY_TEMPLATE_12 = "exec GetAllPeopleWithContactInfo;";
    final static String QUERY_TEMPLATE_13 = "exec GetDonorsWhoAreEmployees;";
    final static String QUERY_TEMPLATE_14 = "exec IncreaseSalaryForEmployeesWithMultipleTeams;";
    final static String QUERY_TEMPLATE_15 = "exec DeleteClients;";
    
    final static String EXTRA_QUERY_TEMPLATE_1 = "exec AddDonation @SSN = ?, @date = ?, @amount = ?, @type = ?, @fundraising_campaign = ?;";
    final static String EXTRA_QUERY_TEMPLATE_2 = "exec AddToReportsTo @volunteer_ssn = ?, @team_name = ?,@reporting_date = ?, @content= ?, @employee_ssn = ?;";
    final static String EXTRA_QUERY_TEMPLATE_3 = "exec AddToWorksIn @volSSN = ?, @team_name = ?,@monthly_hours_worked = ?, @role = ?, @status = ?;";
    

	
	// User input prompt
	final static String PROMPT =
	    "\nPlease select one of the options below: \n" +
	    "1) Enter a new team into the database (1/month). \n" +
	    "2) Enter a new client into the database and associate him or her with one or more teams (1/week). \n" +
	    "3) Enter a new volunteer into the database and associate him or her with one or more teams (2/month). \n" +
	    "4) Enter the number of hours a volunteer worked this month for a particular team (30/month). \n" +
	    "5) Enter a new employee into the database and associate him or her with one or more teams (1/year). \n" +
	    "6) Enter an expense charged by an employee (1/day). \n" +
	    "7) Enter a new donor and associate him or her with several donations (1/day). \n" +
	    "8) Retrieve the name and phone number of the doctor of a particular client (1/week). \n" +
	    "9) Retrieve the total amount of expenses charged by each employee for a particular period of time. The list should be sorted by the total amount of expenses (1/month). \n" +
	    "10) Retrieve the list of volunteers that are members of teams that support a particular client (4/year). \n" +
	    "11) Retrieve the names of all teams that were founded after a particular date (1/month). \n" +
	    "12) Retrieve the names, social security numbers, contact information, and emergency contact information of all people in the database (1/week). \n" +
	    "13) Retrieve the name and total amount donated by donors that are also employees. The list should be sorted by the total amount of the donations, and indicate if each donor wishes to remain anonymous (1/week). \n" +
	    "14) Increase the salary by 10% of all employees to whom more than one team must report (1/year). \n" +
	    "15) Delete all clients who do not have health insurance and whose value of importance for transportation is less than 5 (4/year). \n" +
	    "16) Import: enter new teams from a data file until the file is empty\n" +
	    "17) Export: Retrieve names and mailing addresses of all people on the mailing list and\n"
	    + "output them to a data file instead of screen \n" +
	    "(18) Quit \n";

	
	
	
	public static void main(String[] args) throws SQLException 
	{
		System.out.println("Indiv Proj");
		final Scanner sc = new Scanner(System.in); // Scanner is used to collect the user input
		String option = ""; // Initialize user option selection as nothing
		while (!option.equals("18")) // Ask user for options until option 4 is selected
		{ 
			System.out.println(PROMPT); // Print the available options
			option = sc.next(); // Read in the user option selection
			switch (option)  // Switch between different options
			{
			    case "1": // Enter a new team into the database (1/month)
			    	sc.nextLine();
			        System.out.println("Please enter the team name:");
			        String teamName = sc.nextLine();
			        System.out.println("Please enter the team type:");
			        String teamType = sc.nextLine();
			        System.out.println("Please enter the team creation date (MM/DD/YYYY):");
			        String teamDate = sc.nextLine();
			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_1)) {
			                statement.setString(1, teamName);
			                statement.setString(2, teamType);
			                statement.setString(3, teamDate);
			                System.out.println("Dispatching the query...");
			                final int rowsInserted = statement.executeUpdate();
			                System.out.println(String.format("Done. %d row inserted.", rowsInserted));
			            }
			        }
			        break;
			    case "2": // Enter a new client into the database and associate him or her with one or more teams (1/week)
			        sc.nextLine(); // To consume the leftover newline character
			        System.out.println("Please enter the client SSN:");
			        int clientSSN = sc.nextInt();
			        sc.nextLine(); // Consume newline

			        System.out.println("Enter the client name:");
			        String clientName = sc.nextLine();

			        System.out.println("Enter the client gender:");
			        String clientGender = sc.nextLine();

			        System.out.println("Enter the client profession:");
			        String clientProfession = sc.nextLine();

			        System.out.println("Is the client on the mailing list? (type 1 for true or 0 for false):");
			        int isOnMailingList = sc.nextInt();
			        sc.nextLine(); // Consume newline

			        System.out.println("Enter the doctor name:");
			        String doctorName = sc.nextLine();

			        System.out.println("Enter the doctor phone number:");
			        String doctorPhone = sc.nextLine();

			        System.out.println("Enter the first assignment date (YYYY-MM-DD):");
			        String assignmentDate = sc.nextLine();
			        
			        
			        String dateC = sc.nextLine(); //TODO

			        System.out.println("Enter the shopping needs (1-10):");
			        int shoppingNeeds = sc.nextInt();

			        System.out.println("Enter the visiting needs (1-10):");
			        int visitingNeeds = sc.nextInt();

			        System.out.println("Enter the food needs (1-10):");
			        int foodNeeds = sc.nextInt();

			        System.out.println("Enter the yardwork needs (1-10):");
			        int yardworkNeeds = sc.nextInt();

			        System.out.println("Enter the housekeeping needs (1-10):");
			        int housekeepingNeeds = sc.nextInt();

			        System.out.println("Enter the transportation needs (1-10):");
			        int transportationNeeds = sc.nextInt();
			        sc.nextLine(); // Consume newline

			        System.out.println("Please enter the volunteer SSN:");
			        int volunteerSSNC = sc.nextInt();
			        sc.nextLine(); // Consume newline

			        System.out.println("Please enter the team name:");
			        String teamNameC = sc.nextLine();

			        System.out.println("Please enter the client status: (type 1 for true or 0 for false)");
			        int status = sc.nextInt();

			        System.out.println("Please enter the emergency contact name:");
			        String emerName = sc.nextLine();

			        System.out.println("Please enter the emergency contact phone number:");
			        String emerPhone = sc.nextLine();

			        System.out.println("Please enter the emergency contact relationship:");
			        String emerRelationship = sc.nextLine();

			        System.out.println("Please enter the client mailing address:");
			        String clientMailingAddress = sc.nextLine();

			        System.out.println("Please enter the client phone number:");
			        String clientPhone = sc.nextLine();

			        System.out.println("Please enter the client email address:");
			        String clientEmail = sc.nextLine();

			        System.out.println("Please enter the policy ID:");
			        int policyId = sc.nextInt();
			        sc.nextLine(); // Consume newline

			        System.out.println("Please enter the insurance provider name:");
			        String providerName = sc.nextLine();

			        System.out.println("Please enter the insurance provider address:");
			        String providerAddress = sc.nextLine();

			        System.out.println("Please enter the insurance type:");
			        String insuranceType = sc.nextLine();

			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_2)) {
			                // Setting the parameters
			                statement.setInt(1, clientSSN);
			                statement.setString(2, clientName);
			                statement.setString(3, clientGender);
			                statement.setString(4, clientProfession);
			                statement.setInt(5, isOnMailingList);
			                statement.setString(6, doctorName);
			                statement.setString(7, doctorPhone);
			                statement.setString(8, dateC);
			                statement.setInt(9, shoppingNeeds);
			                statement.setInt(10, visitingNeeds);
			                statement.setInt(11, foodNeeds);
			                statement.setInt(12, yardworkNeeds);
			                statement.setInt(13, housekeepingNeeds);
			                statement.setInt(14, transportationNeeds);
			                statement.setInt(15, volunteerSSNC);
			                statement.setString(16, teamNameC);
			                statement.setInt(17, status);
			                statement.setString(18, emerName);
			                statement.setString(19, emerPhone);
			                statement.setString(20, emerRelationship);
			                statement.setString(21, clientMailingAddress);
			                statement.setString(22, clientPhone);
			                statement.setString(23, clientEmail);
			                statement.setInt(24, policyId);
			                statement.setString(25, providerName);
			                statement.setString(26, providerAddress);
			                statement.setString(27, insuranceType);

			                System.out.println("Dispatching the query...");
			                final int rowsInserted = statement.executeUpdate();
			                System.out.println(String.format("Done. %d row inserted.", rowsInserted));
			            }
			        } catch (SQLException e) {
			            e.printStackTrace();
			        }
			        break;


			    case "3": 
			        sc.nextLine(); 
			        System.out.println("Please enter the volunteer SSN:");
			        int volunteerSSN = sc.nextInt();
			        sc.nextLine(); 

			        System.out.println("Please enter the volunteer name:");
			        String volunteerName = sc.nextLine();

			        System.out.println("Please enter the volunteer gender:");
			        String volunteerGender = sc.nextLine();

			        System.out.println("Please enter the volunteer profession:");
			        String volunteerProfession = sc.nextLine();

			        System.out.println("Is the volunteer on the mailing list? (type 1 for true or 0 for false):");
			        int isOnMailingListV = sc.nextInt();
			        sc.nextLine(); // Consume the newline character

			        System.out.println("Please enter the volunteer date joined (YYYY-MM-DD):");
			        String volunteerJoinDate = sc.nextLine();

			        System.out.println("Please enter the recent training course date (YYYY-MM-DD):");
			        String recentTrainingCourseDate = sc.nextLine(); 

			        System.out.println("Please enter the recent training course location:");
			        String recentTrainingCourseLocation = sc.nextLine();

			        System.out.println("Please enter the emergency contact name:");
			        String emerNameV = sc.nextLine();

			        System.out.println("Please enter the emergency contact phone number:");
			        String emerPhoneV = sc.nextLine();

			        System.out.println("Please enter the emergency contact relationship:");
			        String emerRelationshipV = sc.nextLine();

			        System.out.println("Please enter the volunteer mailing address:");
			        String volMailingAddress = sc.nextLine();

			        System.out.println("Please enter the volunteer phone number:");
			        String volPhone = sc.nextLine();

			        System.out.println("Please enter the volunteer email address:");
			        String volEmail = sc.nextLine();

			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL);
			             final PreparedStatement volunteerStatement = connection.prepareStatement(QUERY_TEMPLATE_3)) {

			            // Setting the parameters for the volunteer stored procedure
			            volunteerStatement.setInt(1, volunteerSSN);
			            volunteerStatement.setString(2, volunteerName);
			            volunteerStatement.setString(3, volunteerGender);
			            volunteerStatement.setString(4, volunteerProfession);
			            volunteerStatement.setInt(5, isOnMailingListV); // is_on_mailing_list as a int
			            volunteerStatement.setString(6, volunteerJoinDate);
			            volunteerStatement.setString(7, recentTrainingCourseDate);
			            volunteerStatement.setString(8, recentTrainingCourseLocation);
			            volunteerStatement.setString(9, "");
			            volunteerStatement.setString(10, emerNameV);
			            volunteerStatement.setString(11, emerPhoneV);
			            volunteerStatement.setString(12, emerRelationshipV);
			            volunteerStatement.setString(13, volMailingAddress);
			            volunteerStatement.setString(14, volPhone);
			            volunteerStatement.setString(15, volEmail);

			            System.out.println("Dispatching the query...");
			            final int rowsInserted = volunteerStatement.executeUpdate();
			            System.out.println(String.format("Done. %d row(s) inserted for the volunteer.", rowsInserted));

			            // Adding teams to Works_In
			            System.out.println("Now assigning teams to this volunteer...");
			            while (true) {
			                System.out.println("Enter the team name (or type 'exit' to finish):");
			                String teamName2 = sc.nextLine();
			                if (teamName2.equalsIgnoreCase("exit")) {
			                    System.out.println("Exiting team assignment.");
			                    break;
			                }
			                
			                

			                System.out.println("Enter monthly hours worked:");
			                int monthlyHoursWorked = sc.nextInt();
			                sc.nextLine(); // Consume the newline character

			                System.out.println("Enter the role:");
			                String role = sc.nextLine();

			                System.out.println("Enter the status (e.g., active/inactive):");
			                String status2 = sc.nextLine();

			                // Assign the volunteer to the team
			                try (PreparedStatement teamStatement = connection.prepareStatement(EXTRA_QUERY_TEMPLATE_3)) {
			                    teamStatement.setInt(1, volunteerSSN); // SSN
			                    teamStatement.setString(2, teamName2); // Team Name
			                    teamStatement.setInt(3, monthlyHoursWorked); // Monthly Hours
			                    teamStatement.setString(4, role); // Role
			                    teamStatement.setString(5, status2); // Status

			                    final int teamRowsInserted = teamStatement.executeUpdate();
			                    System.out.println(String.format("Done. %d row(s) inserted for team '%s'.", teamRowsInserted, teamName2));
			                } catch (SQLException e) {
			                    System.out.println("An error occurred while adding the team.");
			                    e.printStackTrace();
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while adding the volunteer.");
			            e.printStackTrace();
			        }
			        break;

			        
			    case "4": // Update volunteer hours (1/month)
			        sc.nextLine();  // Clear the buffer
			        System.out.println("Please enter the SSN of the volunteer:");
			        String ssn = sc.nextLine();
			        System.out.println("Please enter the team name:");
			        String teamNameV2 = sc.nextLine();
			        System.out.println("Please enter the number of hours to update:");
			        int hours = sc.nextInt();
			        
			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_4)) {
			                statement.setString(1, ssn);      // Set SSN
			                statement.setString(2, teamNameV2); // Set team name
			                statement.setInt(3, hours);       // Set hours

			                System.out.println("Dispatching the query...");
			                final int rowsUpdated = statement.executeUpdate(); // Execute the update
			                System.out.println(String.format("Done. %d row(s) updated.", rowsUpdated));
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while updating the volunteer hours.");
			            e.printStackTrace();
			        }
			        break;

			    case "5": // Add an employee and associate with teams
			        sc.nextLine(); 
			        System.out.println("Please enter the employee's SSN:");
			        String employeeSSN = sc.nextLine();

			        System.out.println("Please enter the employee's name:");
			        String employeeName = sc.nextLine();

			        System.out.println("Please enter the employee's gender:");
			        String employeeGender = sc.nextLine();

			        System.out.println("Please enter the employee's profession:");
			        String employeeProfession = sc.nextLine();

			        System.out.println("Is the employee on the mailing list? (type 1 for true or 0 for false):");
			        int isOnMailingListE = sc.nextInt();
			        sc.nextLine(); // Consume the newline

			        System.out.println("Please enter the employee's salary (ex. 60000.00):");
			        double salary = sc.nextDouble();
			        sc.nextLine(); // Consume the newline

			        System.out.println("Please enter the employee's marital status:");
			        String maritalStatus = sc.nextLine();

			        System.out.println("Please enter the employee's hire date (MM/DD/YYYY):");
			        String hireDate = sc.nextLine();

			        System.out.println("Please enter the volunteer SSN (if applicable):");
			        String volunteerSSNE = sc.nextLine();

			        System.out.println("Please enter the employee's team name:");
			        String teamNameE = sc.nextLine();

			        System.out.println("Please enter the date (MM/DD/YYYY):");
			        String date = sc.nextLine();

			        System.out.println("Please enter the content (optional):");
			        String content = sc.nextLine();

			        System.out.println("Please enter the emergency contact name:");
			        String emerNameE = sc.nextLine();

			        System.out.println("Please enter the emergency contact phone number:");
			        String emerPhoneE = sc.nextLine();

			        System.out.println("Please enter the emergency contact relationship:");
			        String emerRelationshipE = sc.nextLine();

			        System.out.println("Please enter the employee mailing address: ");
			        String empMailingAddress = sc.nextLine();

			        System.out.println("Please enter the employee phone number:");
			        String empPhoneNumber = sc.nextLine();

			        System.out.println("Please enter the employee email address:");
			        String empEmailAddress = sc.nextLine();

			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_5)) {
			                // Setting the parameters for the employee insertion query
			                statement.setString(1, employeeSSN);
			                statement.setString(2, employeeName);
			                statement.setString(3, employeeGender);
			                statement.setString(4, employeeProfession);
			                statement.setInt(5, isOnMailingListE);
			                statement.setDouble(6, salary);
			                statement.setString(7, maritalStatus);
			                statement.setString(8, hireDate);
			                statement.setString(9, volunteerSSNE);
			                statement.setString(10, teamNameE);
			                statement.setString(11, date);
			                statement.setString(12, content);
			                statement.setString(13, emerNameE);
			                statement.setString(14, emerPhoneE);
			                statement.setString(15, emerRelationshipE);
			                statement.setString(16, empMailingAddress);
			                statement.setString(17, empPhoneNumber);
			                statement.setString(18, empEmailAddress);

			                System.out.println("Dispatching the query...");
			                final int rowsInserted = statement.executeUpdate();
			                System.out.println(String.format("Done. %d row(s) inserted for the employee.", rowsInserted));
			            }

			            // Loop for associating the employee with teams
			            while (true) {
			                System.out.println("Do you want to associate the employee with a team? Type 'yes' to continue or 'exit' to finish.");
			                String input = sc.nextLine();
			                if (input.equalsIgnoreCase("exit")) {
			                    System.out.println("Exiting team association.");
			                    break;
			                }

			                System.out.println("Please enter the volunteer SSN:");
			                String volunteerSSN2 = sc.nextLine();

			                System.out.println("Please enter the team name:");
			                String teamName4 = sc.nextLine();

			                System.out.println("Please enter the reporting date (MM/DD/YYYY):");
			                String reportingDate = sc.nextLine();

			                System.out.println("Please enter the content (optional):");
			                String reportContent = sc.nextLine();

			                // Add the association for the employee and team
			                try (PreparedStatement reportsToStatement = connection.prepareStatement(EXTRA_QUERY_TEMPLATE_2)) {
			                    reportsToStatement.setString(1, volunteerSSN2); // Volunteer SSN
			                    reportsToStatement.setString(2, teamName4); // Team Name
			                    reportsToStatement.setString(3, reportingDate); // Reporting Date
			                    reportsToStatement.setString(4, reportContent); // Content
			                    reportsToStatement.setString(5, employeeSSN); // Employee SSN

			                    final int rowsInsertedForReports = reportsToStatement.executeUpdate();
			                    System.out.println(String.format("Done. %d row(s) inserted for team '%s'.", rowsInsertedForReports, teamName4));
			                } catch (SQLException e) {
			                    System.out.println("An error occurred while associating the employee with the team.");
			                    e.printStackTrace();
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while adding the employee or associating with teams.");
			            e.printStackTrace();
			        }
			        break;
		    case "6": // Add an expense (1/month)
		        sc.nextLine(); // Clear the buffer
		        System.out.println("Please enter the employee SSN:");
		        String employeeSSN2 = sc.nextLine();

		        System.out.println("Please enter the expense date (MM/DD/YYYY):");
		        String expenseDate = sc.nextLine();

		        System.out.println("Please enter the expense amount:");
		        double expenseAmount = sc.nextDouble();
		        sc.nextLine(); // Consume the newline

		        System.out.println("Please enter the expense description:");
		        String expenseDescription = sc.nextLine();

		        System.out.println("Connecting to the database...");
		        try (final Connection connection = DriverManager.getConnection(URL)) {
		            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_6)) {
		                // Set the parameters for the SQL query
		                statement.setString(1, employeeSSN2);
		                statement.setString(2, expenseDate);
		                statement.setDouble(3, expenseAmount);
		                statement.setString(4, expenseDescription);

		                System.out.println("Dispatching the query...");
		                final int rowsInserted = statement.executeUpdate();
		                System.out.println(String.format("Done. %d row(s) inserted for the expense.", rowsInserted));
		            }
		        } catch (SQLException e) {
		            System.out.println("An error occurred while adding the expense.");
		            e.printStackTrace();
		        }
		        break;

		    case "7": // Add a new donor to the database (1/month)
		        sc.nextLine(); // Consume the leftover newline character
		        System.out.println("Please enter the donor SSN:");
		        String donorSSN = sc.nextLine();

		        System.out.println("Please enter the donor name:");
		        String donorName = sc.nextLine();

		        System.out.println("Please enter the donor gender:");
		        String donorGender = sc.nextLine();

		        System.out.println("Please enter the donor profession:");
		        String donorProfession = sc.nextLine();

		        System.out.println("Is the donor on the mailing list? (type 1 for true or 0 for false):");
		        int isOnMailingListD = sc.nextInt();
		        sc.nextLine(); // Consume the newline

		        System.out.println("Do you want to stay anonymous?:");
		        String statusOfAnonymity = sc.nextLine();

		        System.out.println("Please enter the emergency contact name:");
		        String emerNameD = sc.nextLine();

		        System.out.println("Please enter the emergency contact phone number:");
		        String emerPhoneD = sc.nextLine();

		        System.out.println("Please enter the emergency contact relationship:");
		        String emerRelationshipD = sc.nextLine();

		        System.out.println("Please enter the donor mailing address:");
		        String donMailingAddress = sc.nextLine();

		        System.out.println("Please enter the donor phone number:");
		        String donPhone = sc.nextLine();

		        System.out.println("Please enter the donor email address:");
		        String donEmail = sc.nextLine();

		        System.out.println("Connecting to the database...");
		        try (final Connection connection = DriverManager.getConnection(URL)) {
		            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_7)) {
		                // Set the donor parameters
		                statement.setString(1, donorSSN);
		                statement.setString(2, donorName);
		                statement.setString(3, donorGender);
		                statement.setString(4, donorProfession);
		                statement.setInt(5, isOnMailingListD);
		                statement.setString(6, statusOfAnonymity);
		                statement.setString(7, emerNameD);
		                statement.setString(8, emerPhoneD);
		                statement.setString(9, emerRelationshipD);
		                statement.setString(10, donMailingAddress);
		                statement.setString(11, donPhone);
		                statement.setString(12, donEmail);
		                System.out.println("Dispatching the query...");
		                final int rowsInserted = statement.executeUpdate();
		                System.out.println(String.format("Done. %d row(s) inserted for the donor.", rowsInserted));
		            }

		            // Add donations in a loop
		            System.out.println("Would you like to add donations for this donor? (Type 'yes' to continue or 'no' to skip)");
		            String addDonations = sc.nextLine();
		            if (addDonations.equalsIgnoreCase("yes")) {
		                while (true) {
		                    System.out.println("Enter 'exit' to stop adding donations or press Enter to continue.");
		                    String input = sc.nextLine();
		                    if (input.equalsIgnoreCase("exit")) {
		                        System.out.println("Exiting the donation loop.");
		                        break;
		                    }

		                    // Collect donation details
		                    System.out.println("Enter the donation date (YYYY-MM-DD):");
		                    String dateD = sc.nextLine();

		                    System.out.println("Enter the donation amount:");
		                    double amount = Double.parseDouble(sc.nextLine());

		                    System.out.println("Enter the donation type (e.g., Cash, Credit Card):");
		                    String type = sc.nextLine();

		                    System.out.println("Enter the fundraising campaign:");
		                    String campaign = sc.nextLine();

		                    // Add the donation for the donor
		                    try (PreparedStatement donationStatement = connection.prepareStatement(EXTRA_QUERY_TEMPLATE_1)) {
		                        donationStatement.setString(1, donorSSN);
		                        donationStatement.setString(2, dateD);
		                        donationStatement.setDouble(3, amount);
		                        donationStatement.setString(4, type);
		                        donationStatement.setString(5, campaign);

		                        donationStatement.executeUpdate();
		                        System.out.println("Donation added successfully!");
		                    } catch (SQLException e) {
		                        System.out.println("Error while adding donation: " + e.getMessage());
		                    }
		                }
		            }
		        } catch (SQLException e) {
		            System.out.println("An error occurred while adding the donor.");
		            e.printStackTrace();
		        }
		        break;
			    case "8": // Get client doctor information (1/month)
			        sc.nextLine(); 
			        System.out.println("Please enter client SSN:");
			        String clientSSN2 = sc.nextLine();

			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_8)) {
			                // Set the client SSN
			                statement.setString(1, clientSSN2);

			                
			                System.out.println("Dispatching the query...");
			                try (final ResultSet resultSet = statement.executeQuery()) {
			                    if (resultSet.next()) {
			                        
			                        System.out.println("Doctor Information:");
			                        System.out.println(String.format("%s | %s |",
			                                resultSet.getString(1), 
			                                resultSet.getString(2) 
			                        ));
			                    } else {
			                        System.out.println("No doctor information found for the given SSN.");
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while retrieving the client doctor information.");
			            e.printStackTrace();
			        }
			        break;

			    case "9": //Retrieve the total amount of expenses charged by each employee for a particular period of
			    	//time. The list should be sorted by the total amount of expenses (1/month) 
			        sc.nextLine(); 
			        System.out.println("Please enter the start date (YYYY-MM-DD):");
			        String startDate = sc.nextLine();

			        System.out.println("Please enter the end date (YYYY-MM-DD):");
			        String endDate = sc.nextLine();

			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            System.out.println("Dispatching the query...");
			            
			           
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_9)) {
			                statement.setString(1, startDate);  // Replace the first '?'
			                statement.setString(2, endDate);    // Replace the second '?'

			                try (final ResultSet resultSet = statement.executeQuery()) {
			                    System.out.println("Employee Expenses:");
			                    System.out.println("SSN | Total Expenses");

			                    // Process the result set and print each tuple
			                    while (resultSet.next()) {
			                        System.out.println(String.format("%d | $%.2f",
			                                resultSet.getInt(1),              // SSN
			                                resultSet.getDouble(2) // expense_amount

			                        ));
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while retrieving employee expenses.");
			            e.printStackTrace();
			        }
		        	break;


			    case "10": // Get volunteers supporting a client (1/month)
			        sc.nextLine(); 
			        System.out.println("Please enter the client SSN:");
			        String clientSSN3 = sc.nextLine();

			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            System.out.println("Dispatching the query...");
			            
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_10)) {
			                
			                statement.setString(1, clientSSN3);
			                
			                try (final ResultSet resultSet = statement.executeQuery()) {
			                    System.out.println("Supporting Volunteers:");
			                    
			                    while (resultSet.next()) {
			                        System.out.println(String.format("%s",
			                                resultSet.getString(1)

			                        ));
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while retrieving teams founded after the given date.");
			            e.printStackTrace();
			        }
			        break;




			    case "11": // Get teams founded after a certain date (1/month)
			        sc.nextLine(); 
			        System.out.println("Please enter the date (YYYY-MM-DD):");
			        String date3 = sc.nextLine();

			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            System.out.println("Dispatching the query...");
			            
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_11)) {
			                // Set the date parameter
			                statement.setString(1, date3);
			                
			                try (final ResultSet resultSet = statement.executeQuery()) {
			                    System.out.println("Teams Founded After Given Date:");
			                    
			                    System.out.println("Team Name:");
			                    while (resultSet.next()) {
			                        System.out.println(String.format("%s",
			                                resultSet.getString(1)

			                        ));
			                    }
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while retrieving teams founded after the given date.");
			            e.printStackTrace();
			        }
			        break;


			    case "12": // Get all people with contact info (1/month)
			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            System.out.println("Dispatching the query...");
			            
			            try (final Statement statement = connection.createStatement();
			                 final ResultSet resultSet = statement.executeQuery(QUERY_TEMPLATE_12)) {
			                
			                System.out.println("All People with Contact Info:");
			               
			                System.out.println("Name | SSN | Mailing Address | Phone_Number | Email Address | Emergency Contact Name| Emergency Contact Phone | Relationship");
			                while (resultSet.next()) {
			                    System.out.println(String.format("%s | %s | %s | %s | %s | %s | %s | %s ",
			                            resultSet.getString(1),
			                            resultSet.getString(2),
			                            resultSet.getString(3),  
			                            resultSet.getString(4), 
			                            resultSet.getString(5), 
			                            resultSet.getString(6), 
			                            resultSet.getString(7), 
			                            resultSet.getString(8)
			                            
			                            
			                    ));
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while retrieving people with contact info.");
			            e.printStackTrace();
			        }
			        break;


			    case "13": // Get donors who are employees (1/month)
			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            System.out.println("Dispatching the query...");
			            
			            try (final Statement statement = connection.createStatement();
			                 final ResultSet resultSet = statement.executeQuery(QUERY_TEMPLATE_13)) {
			                
			                System.out.println("Donors Who Are Employees:");
			               
			                System.out.println("Donor Name | Total Donations");
			                while (resultSet.next()) {
			                    System.out.println(String.format("%s | %f ",
			                            resultSet.getString(1),
			                            resultSet.getDouble(2)  
			                    ));
			                }
			            }
			        } catch (SQLException e) {
			            System.out.println("An error occurred while retrieving donors who are employees.");
			            e.printStackTrace();
			        }
			        break;


			    case "14": // Increase the salary by 10% of all employees to whom more than one team must report (1/year)
			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_14) ){
			                System.out.println("Dispatching the query...");
			                final int rowsUpdated = statement.executeUpdate();
			                System.out.println(String.format("Done. %d row(s) updated.", rowsUpdated));
			            }
			        }
			        break;

			    case "15": // TODO SQL
			        System.out.println("Connecting to the database...");
			        try (final Connection connection = DriverManager.getConnection(URL)) {
			            try (final PreparedStatement statement = connection.prepareStatement(QUERY_TEMPLATE_15)) {
			                System.out.println("Dispatching the query...");
			                final int rowsDeleted = statement.executeUpdate();
			                System.out.println(String.format("Done. %d row(s) deleted.", rowsDeleted));
			            }
			        }
			        break;

			    case "16": 
			        System.out.println("Importing data...");
			        
			        break;

			    case "17": // Export data
			        System.out.println("Exporting data...");
			        System.out.println("Please enter the name of the output file (e.g., output.txt):");
			        String outputFileName = sc.nextLine();

			        
			        final String EXPORT_QUERY = "SELECT p.name, ci.mailing_address FROM Person p JOIN Contact_Information ci ON p.SSN = ci.SSN WHERE p.is_on_mailing_list = 1";

			        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			             Statement statement = connection.createStatement();
			             ResultSet resultSet = statement.executeQuery(EXPORT_QUERY);
			             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {

			            System.out.println("Connected to the database. Fetching data...");

			            
			            writer.write("Name, Mailing Address");
			            writer.newLine();

			            
			            while (resultSet.next()) {
			                String name = resultSet.getString("name");
			                String mailingAddress = resultSet.getString("mailing_address");

			                writer.write(String.format("%s, %s", name, mailingAddress));
			                writer.newLine();
			            }

			            System.out.println("Data successfully exported to " + outputFileName);

			        } catch (SQLException e) {
			            System.out.println("An error occurred while fetching data from the database.");
			            e.printStackTrace();
			        } catch (IOException e) {
			            System.out.println("An error occurred while writing to the file.");
			            e.printStackTrace();
			        }
			        break;

			    case "18": // Quit
			        System.out.println("Exiting the program.");
			        System.exit(0); // Terminate the program
			        break;

			    default:
			        System.out.println("Invalid selection. Please try again.");
			        break;
			}

			}
		sc.close();
		}
	
	}
