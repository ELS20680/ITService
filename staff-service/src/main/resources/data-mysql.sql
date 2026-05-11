-- Populate staff records.
-- MySQL in Linux containers is case-sensitive for table names.
INSERT INTO staff (staff_id, first_name, last_name, email, staff_role) VALUES
                                                                           ('STAFF-101', 'David', 'Miller', 'd.miller@it-support.com', 'AGENT'),
                                                                           ('STAFF-102', 'Elena', 'Rodriguez', 'e.rod@it-support.com', 'ADMIN');
