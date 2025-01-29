
drop procedure if exists AddNewDonor;
drop table if exists Check_Payment;
drop table if exists Card_Payment;
drop type if exists DonType;
drop table if exists Donation;
drop table if exists Expenses;
drop table if exists Cares_For;
drop table if exists Reports_To;
drop table if exists Works_In;
drop table if exists Team;
drop table if exists Volunteer;
drop table if exists Owns;
drop table if exists Insurance_Policy;
drop table if exists Client;
drop table if exists Contact_Information;
drop table if exists Emergency_Contact;
drop table if exists Donor;
drop table if exists Employee;
drop table if exists Person;



declare @i int = 0

create table Person (
    SSN int primary key clustered,
    name nvarchar(64),
    gender nvarchar(10),
    profession nvarchar(64),
    is_on_mailing_list bit
);

create table Emergency_Contact (
    SSN int,
    name nvarchar(64),
    phone_number nvarchar(15),
    relationship nvarchar(32),
    foreign key (SSN) references Person(SSN)
);
create nonclustered index idx_EC_SSN on Emergency_Contact(SSN);

create table Contact_Information (
    SSN int,
    mailing_address nvarchar(255),
    phone_number nvarchar(15),
    email_address nvarchar(64),
    foreign key (SSN) references Person(SSN)
);
create nonclustered index idx_CI_SSN on Contact_Information(SSN);


create table Client (
    client_SSN int primary key clustered,
    doctor_name nvarchar(64),
    doctor_phone_number nvarchar(15),
    first_assignment_date date,
    shopping_needs int check (shopping_needs between 1 and 10),  
    visiting_needs int check (visiting_needs between 1 and 10),
    food_needs int check (food_needs between 1 and 10),
    yardwork_needs int check (yardwork_needs between 1 and 10),
    housekeeping_needs int check (housekeeping_needs between 1 and 10),
    transportation_needs int check (transportation_needs between 1 and 10),
    foreign key (client_SSN) references Person(SSN)
);


create table Insurance_Policy (
    policy_id int primary key nonclustered,
    provider_name nvarchar(64),
    provider_address nvarchar(255),
    insurance_type nvarchar(64)
);

create nonclustered index IX_Insurance_Type on Insurance_Policy(insurance_type);

create table Owns (
    policy_id int,
    client_SSN int,
    foreign key (policy_id) references Insurance_Policy(policy_id),
    foreign key (client_SSN) references Client(client_SSN)
);
create nonclustered index idx_Owns_SSN on Owns(client_SSN);

create table Volunteer (
    vol_SSN int primary key clustered,
    date_joined date,
    recent_training_course_date date,
    recent_training_course_location nvarchar(64),
    foreign key (vol_SSN) references Person(SSN)
);
create table Team (
    team_name nvarchar(64),
    team_type nvarchar(64),
    date date,
    primary key (team_name)
);
create table Employee (
    emp_SSN int primary key clustered,
    salary decimal(10, 2),
    marital_status nvarchar(16),
    hire_date date,
    foreign key (emp_SSN) references Person(SSN)
);
create table Works_In (
    SSN int,
    name nvarchar(64),
    monthly_hours_worked int,
    role nvarchar(64),
    status nvarchar(32),
    foreign key (SSN) references Volunteer(vol_SSN),
    foreign key (name) references Team(team_name)
);


create table Reports_To (
    volunteer_SSN int,
    team_name nvarchar(64),
    date date,
    content nvarchar(255),
    employee_SSN int,
    foreign key (volunteer_SSN) references Volunteer(vol_SSN),
    foreign key (employee_SSN) references Employee(emp_SSN),
    constraint reportingTeam foreign key (team_name) references Team(team_name)
);
create nonclustered index idx_RT_volunteer_SSN on Reports_To(volunteer_SSN);


create table Cares_For (
    volunteer_SSN int,
    name nvarchar(64),
    status bit,
    client_SSN int,
    foreign key (volunteer_SSN) references Volunteer(vol_SSN),
    foreign key (client_SSN) references Client(client_SSN)
);




create table Expenses (
    SSN int,
    date date,
    amount decimal(10, 2),
    description nvarchar(255),
    foreign key (SSN) references Employee(emp_SSN)
);
create clustered index idx_Expenses_SSN on Expenses(SSN);

create table Donor (
    donr_SSN int primary key clustered,
    status_of_anonymity bit,
    foreign key (donr_SSN) references Person(SSN)
);

create table Donation (
    SSN int,
    date date,
    amount decimal(10, 2),
    type nvarchar(32),
    fundraising_campaign nvarchar(64),
    primary key nonclustered (SSN, date)
);

create type DonType AS TABLE
(
    date date,
    amount decimal(10, 2),
    type nvarchar(32),
    fundraising_campaign nvarchar(64)
);

create table Card_Payment (
    SSN int,
    date date,
    amount decimal(10, 2),
    card_number nvarchar(16),
    card_expiry_date date,
    card_type nvarchar(16),
    foreign key (SSN, date) references Donation(SSN, date)
);
create nonclustered index idx_cp_SSN on Card_Payment(SSN);

create table Check_Payment (
    SSN int,
    date date,
    amount decimal(10, 2),
    check_number nvarchar(16),
    foreign key (SSN, date) references Donation(SSN, date)
);
create nonclustered index idx_ChP_SSN on Check_Payment(SSN);

--basic proced.
go
drop procedure if exists AddEmergencyContacts
go

create procedure AddEmergencyContacts(
    @SSN int,
    @name nvarchar(64),
    @phone_number nvarchar(15),
    @relationship nvarchar(32)
)
as
begin
    insert into Emergency_Contact(SSN, name, phone_number, relationship)
    values(@SSN, @name, @phone_number, @relationship)
end;

go
drop procedure if exists addContactInfo;
go
create procedure addContactInfo(
    @SSN int,
    @mailing_address nvarchar(255),
    @phone_number nvarchar(15),
    @email_address nvarchar(64)
)
as
begin
    insert into Contact_Information(SSN, mailing_address, phone_number, email_address)
    values(@SSN, @mailing_address, @phone_number, @email_address);
end;

go
drop procedure if exists ClientInsurance 
go

create procedure ClientInsurance(
    @policy_id int,
    @provider_name nvarchar(64),
    @provider_address nvarchar(255),
    @insurance_type nvarchar(64)
)
as
begin
    insert into Insurance_Policy(policy_id, provider_name, provider_address, insurance_type)
    values(@policy_id, @provider_name, @provider_address, @insurance_type);
end;
go
drop procedure if exists AddDonation 
go

create procedure AddDonation
    @SSN int,
    @date date,
    @amount decimal(10, 2),
    @type nvarchar(32),
    @fundraising_campaign nvarchar(64)



AS
BEGIN
    -- Insert a single donation into the Donations table
    INSERT INTO Donation (SSN, date, amount, type, fundraising_campaign)
    VALUES (@SSN, @date, @amount, @type, @fundraising_campaign);


END;
GO

GO
DROP PROCEDURE IF EXISTS AddToWorksIn;
GO

CREATE PROCEDURE AddToWorksIn
    @volSSN INT,
    @team_name NVARCHAR(64),
    @monthly_hours_worked INT,
    @role NVARCHAR(64),
    @status NVARCHAR(32)
AS
BEGIN
    -- Insert into Works_In table
    insert into Works_In (SSN, name, monthly_hours_worked, role, status)
    VALUES (@volSSN, @team_name, @monthly_hours_worked, @role, @status);

END;
GO


GO
DROP PROCEDURE IF EXISTS AddToReportsTo;
GO

CREATE PROCEDURE AddToReportsTo
    @volunteer_ssn INT,
    @team_name NVARCHAR(64),
    @reporting_date DATE,
    @content NVARCHAR(255),
    @employee_ssn INT
AS
BEGIN
    -- Insert into Reports_To table
    INSERT INTO Reports_To (volunteer_SSN, team_name, date, content, employee_SSN)
    VALUES (@volunteer_ssn, @team_name, @reporting_date, @content, @employee_ssn);

END;
GO






--query 1: make a new team
go
drop procedure if exists AddNewTeam;
GO

create procedure AddNewTeam(
    @team_name nvarchar(50),
    @team_type nvarchar(50),
    @date date
)
as
begin
    insert into Team (team_name, team_type, date)
    values (@team_name, @team_type, @date);
end
Go
--query 2: add a new client and associate with team
drop procedure if exists AddNewClient;
go
create procedure AddNewClient(
    --client info
    @client_SSN int,
    @name nvarchar(50),
    @gender nvarchar(10),
    @profession nvarchar(50),
    @is_on_mailing_list bit,
    @doctor_name nvarchar(50),
    @doctor_phone_number nvarchar(15),
    @first_assignment_date date,
    @shopping_needs int,
    @visiting_needs int,
    @food_needs int,
    @yardwork_needs int,
    @housekeeping_needs int,
    @transportation_needs int,

    --team info
    @volunteer_SSN int,
    @team_name nvarchar(64),
    @status bit,

    --emergency contact
    @emer_name nvarchar(64),
    @emer_phone_number nvarchar(15),
    @emer_relationship nvarchar(32),

    --contact info
    @client_mailing_address nvarchar(255),
    @client_phone_number nvarchar(15),
    @client_email_address nvarchar(64),

    --insurance policy
    @policy_id int,
    @provider_name nvarchar(64),
    @provider_address nvarchar(255),
    @insurance_type nvarchar(64)
)
as
begin
    insert into Person (SSN, name, gender, profession, is_on_mailing_list)
    values (@client_SSN, @name, @gender, @profession, @is_on_mailing_list);
    
    insert into Client (client_SSN, doctor_name, doctor_phone_number, first_assignment_date, shopping_needs, visiting_needs, food_needs, yardwork_needs, housekeeping_needs, transportation_needs)
    values (@client_SSN, @doctor_name, @doctor_phone_number, @first_assignment_date, @shopping_needs, @visiting_needs, @food_needs, @yardwork_needs, @housekeeping_needs, @transportation_needs);

    insert into Cares_For(volunteer_SSN, name, status, client_SSN)
    values(@volunteer_SSN, @name, @status, @client_SSN);

    --emergency contanct
    exec AddEmergencyContancts @client_SSN, @emer_name, @emer_phone_number, @emer_relationship;
    --contact info
    exec addContanctInfo @client_SSN,@client_mailing_address,
     @client_phone_number, @client_email_address
    
    --insurance policy
    exec ClientInsurance @policy_id, @provider_name, @provider_address,
    @insurance_type
    insert into Owns(policy_id, client_SSN)
    values(@policy_id, @client_SSN)

end


--query 3: add a new volunteer and associate with teams
go
drop procedure if exists AddNewVolunteer;
GO
create procedure AddNewVolunteer(
    --values to add new volunteer
    @volunteer_SSN int,
    @name nvarchar(50),
    @gender nvarchar(10),
    @profession nvarchar(50),
    @is_on_mailing_list bit,
    @date_joined date,
    @recent_training_course_date date,
    @recent_training_course_location nvarchar(50),
    -- information to associate with team
    @team_name nvarchar(50),

    --emergency contact
    @emer_name nvarchar(64),
    @emer_phone_number nvarchar(15),
    @emer_relationship nvarchar(32),

    --contact info
    @vol_mailing_address nvarchar(255),
    @vol_phone_number nvarchar(15),
    @vol_email_address nvarchar(64)
)
as
begin

        -- insert person
        insert into Person (SSN, name, gender, profession, is_on_mailing_list)
        values (@volunteer_SSN, @name, @gender, @profession, @is_on_mailing_list);
    
    insert into Volunteer (vol_SSN, date_joined, recent_training_course_date, recent_training_course_location)
    values (@volunteer_SSN, @date_joined, @recent_training_course_date, @recent_training_course_location);

    insert into Works_In (SSN, name, monthly_hours_worked, role, status)
    values (@volunteer_SSN, @team_name, 0, 'member', 'active');

    --emergency contanct
    exec AddEmergencyContancts @volunteer_SSN, @emer_name, @emer_phone_number, @emer_relationship;
    --contact info
    exec addContanctInfo @volunteer_SSN,@vol_mailing_address, @vol_phone_number,
     @vol_email_address;
end;

--query 4: update monthly hours worked on a team
go
drop procedure if exists UpdateVolunteerHours;
GO
create procedure UpdateVolunteerHours(
    @SSN int,
    @team_name nvarchar(50),
    @hours int
)
as
begin
    update Works_In
    set monthly_hours_worked = monthly_hours_worked + @hours
    where SSN = @SSN and name = @team_name;
end;

--query 5: Enter a New Employee and Associate with Teams
go
drop procedure if exists AddNewEmployee;
GO
create procedure AddNewEmployee(
    -- Data to add new employee
    @employee_SSN int,
    @name nvarchar(50),
    @gender nvarchar(10),
    @profession nvarchar(50),
    @is_on_mailing_list bit,
    @salary decimal(10, 2),
    @marital_status nvarchar(10),
    @hire_date date,
    -- Data to associate with team
    @volunteer_SSN int,
    @team_name nvarchar(50),
    @date date,
    @content nvarchar(255),
    --emergency contact
    @emer_name nvarchar(64),
    @emer_phone_number nvarchar(15),
    @emer_relationship nvarchar(32),

    --contact info
    @emp_mailing_address nvarchar(255),
    @emp_phone_number nvarchar(15),
    @emp_email_address nvarchar(64)
)
as
begin
    -- insert employee into Person table

        -- insert person
        insert into Person (SSN, name, gender, profession, is_on_mailing_list)
        values (@employee_SSN, @name, @gender, @profession, @is_on_mailing_list);
        
    -- employee table
    insert into Employee (emp_SSN, salary, marital_status, hire_date)
    values (@employee_SSN, @salary, @marital_status, @hire_date);


    --emergency contact
    exec AddEmergencyContancts @employee_SSN, @emer_name, @emer_phone_number, @emer_relationship;
    
    --contact info
    exec addContanctInfo @employee_SSN,@emp_mailing_address, @emp_phone_number,
    @emp_email_address;

    -- Step 5: Insert into Reports_To table
    insert into Reports_To (volunteer_SSN, team_name, date, content, employee_SSN)
    values (@volunteer_SSN, @team_name, @date, @content, @employee_SSN);
end;

--q6: add expense
go
drop procedure if exists AddExpense;
go
create procedure AddExpense(
    @SSN int,
    @date date,
    @amount decimal(10, 2),
    @description nvarchar(255)
)
as
begin
    insert into Expenses (SSN, date, amount, description)
    values (@SSN, @date, @amount, @description);
end;

--q7 add new donor
go
drop procedure if exists AddNewDonor;
go
create procedure AddNewDonor(
    --donor info
    @don_SSN int,
    @name nvarchar(50),
    @gender nvarchar(10),
    @profession nvarchar(50),
    @is_on_mailing_list bit,
    @status_of_anonymity bit,

    --emergency contact
    @emer_name nvarchar(64),
    @emer_phone_number nvarchar(15),
    @emer_relationship nvarchar(32),

    --contact info
    @don_mailing_address nvarchar(255),
    @don_phone_number nvarchar(15),
    @don_email_address nvarchar(64)
)
as
begin
    --new person if no SSN
    if not exists (select 1 from Person where SSN = @don_SSN)
    begin
        -- insert person
        insert into Person (SSN, name, gender, profession, is_on_mailing_list)
        values (@don_SSN, @name, @gender, @profession, @is_on_mailing_list);
    end;

    -- donor
    insert into Donor (donr_SSN, status_of_anonymity)
    values (@don_SSN, @status_of_anonymity);

   --emergency contact
    exec AddEmergencyContancts @don_SSN, @emer_name, @emer_phone_number, @emer_relationship;
    
    --contact info
    exec addContanctInfo @don_SSN,@don_mailing_address,@don_phone_number,@don_email_address;

END;


--q8 doctor info
go
drop procedure if exists GetClientDoctorInfo;
go
create procedure GetClientDoctorInfo(
    @SSN int
)
as
begin
    select doctor_name, doctor_phone_number
    from Client
    where client_SSN = @SSN;
end;

--q9 Retrieve Total Expenses Charged by Each Employee for a Period
go
drop procedure if exists GetEmployeeExpenses;
go
create procedure GetEmployeeExpenses(
    @start_date date,
    @end_date date
)
as
begin
    select e.emp_SSN, sum(exp.amount) as total_expenses
    from Employee e
    join Expenses exp on e.emp_SSN = exp.SSN
    where exp.date between @start_date and @end_date
    group by e.emp_SSN
    order by total_expenses desc;
end;

--q10 Retrieve the List of Volunteers Supporting a Particular Client
go
drop procedure if exists GetVolunteersSupportingClient;
go
create procedure GetVolunteersSupportingClient(
    @client_SSN int
)
as
begin
    select distinct p.name
    from Person p
    join Volunteer v on p.SSN = v.vol_SSN
    where p.SSN = @client_SSN; 
end;

--q11 Retrieve Names of Teams Founded After a Particular Date
go
drop procedure if exists GetTeamsFoundedAfter;
go
create procedure GetTeamsFoundedAfter(
    @date date
)
as
begin
    select team_name
    from Team
    where date > @date;
end;

--q12: Retrieve Names, SSNs, and Emergency Contact Information of All People
go
drop procedure if exists GetAllPeopleWithContactInfo;
go
create procedure GetAllPeopleWithContactInfo
as
begin
    select 
        p.name, 
        p.SSN, 
        ci.mailing_address, 
        ci.phone_number, 
        ci.email_address,
        ec.name as emergency_contact_name, 
        ec.phone_number as emergency_contact_phone, 
        ec.relationship
    from Person p
    left join Contact_Information ci on p.SSN = ci.SSN
    left join Emergency_Contact ec on p.SSN = ec.SSN;
end;

--q13: Retrieve Donors Who Are Also Employees
go
drop procedure if exists GetDonorsWhoAreEmployees;
go
create procedure GetDonorsWhoAreEmployees
 
as
begin  
SELECT p.name, 
       (SELECT SUM(amount)
        FROM Donation 
        WHERE SSN = d.donr_SSN) AS total_donations
FROM Person p
JOIN Employee e ON p.SSN = e.emp_SSN
JOIN Donor d ON e.emp_SSN = d.donr_SSN;
end;





--q14: Increase Salary for Employees Managing Multiple Teams
go
drop procedure if exists IncreaseSalaryForEmployeesWithMultipleTeams;
go
create procedure IncreaseSalaryForEmployeesWithMultipleTeams
as
begin
    -- ssn of remp
    -- select e.emp_SSN
    -- from Employee e
    -- join Reports_To r on e.emp_SSN = r.employee_SSN
    -- group by e.emp_SSN
    -- having count(distinct r.team_name) > 1;

    -- Update salaries
    update Employee
    set salary = salary * 1.10
    where emp_SSN in(
        select e.emp_SSN
        from Employee e
        join Reports_To r on e.emp_SSN = r.employee_SSN
        group by e.emp_SSN
        having count(distinct r.team_name) > 1
    );

end;
--q15: Delete clients with no health insurance or trans < 5
GO
DROP PROCEDURE IF EXISTS DeleteClients;
GO

CREATE PROCEDURE DeleteClients
AS
BEGIN
    -- Step 1: Delete dependent rows in Cares_For table
    DELETE FROM Cares_For 
    WHERE client_SSN IN 
    (
        SELECT c.client_SSN
        FROM Client c
        LEFT JOIN Owns o ON c.client_SSN = o.client_SSN
        LEFT JOIN Insurance_Policy ip ON o.policy_id = ip.policy_id
        WHERE c.transportation_needs < 5 OR ip.insurance_type <> 'health'
    );

    -- Step 2: Delete dependent rows in Owns table
    DELETE FROM Owns 
    WHERE client_SSN IN 
    (
        SELECT c.client_SSN
        FROM Client c
        LEFT JOIN Owns o ON c.client_SSN = o.client_SSN
        LEFT JOIN Insurance_Policy ip ON o.policy_id = ip.policy_id
        WHERE c.transportation_needs < 5 OR ip.insurance_type <> 'health'
    );

    -- Step 3: Delete from the Client table
    DELETE FROM Client
    WHERE transportation_needs < 5 
       OR client_SSN IN (
            SELECT client_SSN
            FROM Owns o
            JOIN Insurance_Policy ip ON o.policy_id = ip.policy_id
            WHERE ip.insurance_type <> 'health'
       );
END;




