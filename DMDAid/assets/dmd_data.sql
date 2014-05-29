CREATE TABLE menus( _id integer primary key autoincrement not null, list_item text , list_type text, next_type text, next text);
CREATE TABLE report_images (_id integer primary key autoincrement not null, image_name text not null, report_id integer not null);
CREATE TABLE reports ( _id integer primary key autoincrement not null, category text not null, report_name text not null, report_date text not null, type text not null, pdfpath text, dirty integer );
CREATE TABLE medication( _id integer primary key autoincrement not null, name text, dose integer, units text, times integer, timesper text, startmonth text, endmonth text, type text, dirty integer );

insert into reports (_id,category,report_name,report_date,type) values (30000,'asd','asd','asd','img');
delete from reports;

insert into medication (_id) values (30000);
delete from medication; 

insert into menus values(null,'Family Member and Primary Care Giver Information','DMDAid','custompage','FAMINFO'); 
insert into menus values(null,'Personal Health Records','DMDAid','list','Personal Health Records'); 
insert into menus values(null,'Current Care Guidelines and Standards','DMDAid','custompage','CAREGUIDE'); 
insert into menus values(null,'Hospital Stay Log','DMDAid','custompage','HOSPITALLOG'); 
insert into menus values(null,'Acute Care Algorithm','DMDAid','custompage','CAREALGO'); 
insert into menus values(null,'Quality of Life Statement','DMDAid','custompage','QOL');
insert into menus values(null,'Medications','DMDAid','Medications','med');
insert into menus values(null,'OTC Meds','DMDAid','Medications','otc');
 
insert into menus values(null,'Neurology','Personal Health Records','list','Neurology'); 
insert into menus values(null,'Respiratory/Pulmonology','Personal Health Records','list','Respiratory/Pulmonology'); 
insert into menus values(null,'Gastrointestinal/Nutrition','Personal Health Records','list','Gastrointestinal/Nutrition'); 
insert into menus values(null,'Cardiology','Personal Health Records','list','Cardiology'); 
insert into menus values(null,'Musculoskeletal','Personal Health Records','list','Musculoskeletal'); 
insert into menus values(null,'Other','Personal Health Records','list','Other'); 


insert into menus values(null,'Uploadable Medical Records','Neurology','report','Neurology Records'); 
insert into menus values(null,'Current Medications and prescriptions','Neurology','custompage','NeurologyMeds'); 
insert into menus values(null,'Current Homeopathic Medications','Neurology','custompage','NeurologyHomeo'); 
insert into menus values(null,'Home Care Regimen','Neurology','custompage','NeurologyRegimen'); 
insert into menus values(null,'Clinic Neurologist Contact Information','Neurology','contact','Neurologist'); 

insert into menus values(null,'Uploadable Medical Records','Respiratory/Pulmonology','report','Respiratory/Pulmonology Records'); 
insert into menus values(null,'Day to Day Home Care regimen','Respiratory/Pulmonology','custompage','Respiratory/Pulmonology'); 
insert into menus values(null,'Current Medications and Prescriptions','Respiratory/Pulmonology','custompage','Respiratory/Pulmonology'); 
insert into menus values(null,'Current Homeopathic Medications','Respiratory/Pulmonology','custompage','Respiratory/Pulmonology'); 
insert into menus values(null,'Ventilator Settings','Respiratory/Pulmonology','custompage','Respiratory/Pulmonology'); 
insert into menus values(null,'Current Pulmonary Functional Test Values','Respiratory/Pulmonology','custompage','Respiratory/Pulmonology'); 
insert into menus values(null,'Home Care regimen during respiratory infection','Respiratory/Pulmonology','custompage','Respiratory/Pulmonology'); 
insert into menus values(null,'Pulmonologist Contact Info','Respiratory/Pulmonology','contact','Respiratory/Pulmonology'); 
insert into menus values(null,'Respiratory Therapist Contact Info','Respiratory/Pulmonology','contact','Respiratory/Pulmonology'); 


insert into menus values(null,'Uploadable Medical Records','Gastrointestinal/Nutrition','report','Gastrointestinal/Nutrition Records'); 
insert into menus values(null,'Current Medications and Prescriptions','Gastrointestinal/Nutrition','custompage','Gastrointestinal/Nutrition'); 
insert into menus values(null,'Homeopathic Medications','Gastrointestinal/Nutrition','custompage','Gastrointestinal/Nutrition'); 
insert into menus values(null,'Home Care Regimen and Nutritional Plan','Gastrointestinal/Nutrition','custompage','Gastrointestinal/Nutrition'); 
insert into menus values(null,'Gastroenterologist Contact Information','Gastrointestinal/Nutrition','contact','Gastrointestinal/Nutrition'); 
insert into menus values(null,'Nutritionist Contact Information','Gastrointestinal/Nutrition','contact','Gastrointestinal/Nutrition'); 


insert into menus values(null,'Uploadable Medical Records','Cardiology','report','Cardiology Records'); 
insert into menus values(null,'Current Medications and Prescriptions','Cardiology','custompage','Cardiology'); 
insert into menus values(null,'Most recent Echo Report: SV, EF, SF','Cardiology','custompage','Cardiology');
insert into menus values(null,'Home Care Regimen','Cardiology','custompage','Cardiology');
insert into menus values(null,'Homeopathic Medications','Cardiology','custompage','Cardiology'); 
insert into menus values(null,'Cardiologist Contact information','Cardiology','contact','Cardiology'); 

insert into menus values(null,'Uploadable Medical Records','Musculoskeletal','report','Musculoskeletal Records'); 
insert into menus values(null,'Current Medications and Prescriptions','Musculoskeletal','custompage','Musculoskeletal'); 
insert into menus values(null,'Homeopathic Medications ','Musculoskeletal','custompage','Musculoskeletal'); 
insert into menus values(null,'Wheelchair description and daily regimen','Musculoskeletal','custompage','Musculoskeletal'); 
insert into menus values(null,'Orthopedist Contact Information','Musculoskeletal','contact','Musculoskeletal'); 

insert into menus values(null,'Uploadable Medical Reports','Other','report','Other'); 


