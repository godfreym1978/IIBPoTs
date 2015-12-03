Create an ODBC connection called XE. Setup the mqsisetdbparms as follows 

mqsisetdbparms IIB.INT -n XE -u <user> -p <password>

mqsireload IIB.INT -e default


Script to insert a row in the database
INSERT INTO DBINPUT_CUSTOMER VALUES ('cust1', 'Fred', 'Flintstone', 'Dev');

Script to update a row in the database
UPDATE DBINPUT_CUSTOMER SET FIRSTNAME = 'Barney', LASTNAME = 'Rubble' WHERE PKEY='cust1';

Script to delete a row in the database
DELETE FROM DBINPUT_CUSTOMER WHERE PKEY='cust1';