create database Coffee
go
use Coffee
go
create table admin
(
	Username varchar(50) primary key,
	[Password] varchar(20) not null
)
insert into admin
values('admin','admin')
create table employe
(
	UsenameEmp varchar(20) primary key,
	[Password] varchar(20) not null,
	HoTen nvarchar(50) not null,
	GioiTinh nvarchar(5) not null,
	SDT char(11) not null,
	DiaChi varchar(20)
)
insert into employe
values ('minhieu','nhanvien',N'Lê Lương Minh Hiếu', 'Nam','0583574399','02 Thanh Sơn'),
	   ('thanhduc','nhanvien',N'Lê Thành Đức','Nam','0908050301','02 Thanh Sơn')
go
create table Kho
(
	maKH varchar(11) primary key,
	tenKH nvarchar(20) not null,
	soluong int not null,
	DVT nvarchar(10) not null,
	dongia int not null,
	ngaynhap char(11) not null
)
insert into Kho
values	('KH0001',N'Cam Tươi',10,N'Quả',100000,'20/05/2021'),
		('KH0002',N'Cafe Trung Nguyên',20,N'Túi',500000,'17/05/2021'),
		('KH0003',N'Cocacola',50,N'Lon',350000,'23/05/2021'),
		('KH0004',N'Đường',5,N'Túi',110000,'02/05/2021')
create table Loai
(
	maloai varchar(11) primary key,
	tenloai nvarchar(20) not null
)
insert into Loai
values	('LCF001',N'Cà Phê'),
		('LNE001',N'Nước Ép'),
		('LST001',N'Sinh Tố'),
		('LCS001',N'Nước Có Sẵn'),
		('LPC001',N'Pha Chế'),
		('LTS001',N'Trà Sữa'),
		('LTTC01',N'Trà Trái Cây')
create table ThucDon
(
	MaMon varchar(11) primary key,
	TenMon nvarchar(50) not null,
	maloai varchar(11) foreign key (maloai) references Loai(maloai),
	DonGia int not null,
)
insert into ThucDon
values	('CF0001',N'Cafe Đen','LCF001',10000),
		('CF0002',N'Cafe Sữa','LCF001',12000),
		('NE0001',N'Cam Ép','LNE001',20000),
		('BX0001',N'Bạc Xĩu','LPC001',20000),
		('ST0001',N'Sinh Tố Bơ','LST001',25000),
		('NE0002',N'Dứa Ép','LNE001',15000),
		('NE0003',N'Dâu Ép','LNE001',15000),
		('CC0001',N'Capuchino','LPC001',25000),
		('TD0001',N'Trà Đào','LTTC01',15000),
		('TS0001',N'Trà Sữa Dâu','LTS001',15000),
		('NN0001',N'Cocacola','LCS001',10000)
create table HoaDon
(
	MaHD char(11) primary key,
	NgayDat varchar(11),
	UsenameEmp varchar(20) foreign key (UsenameEmp) REFERENCES Employe(UsenameEmp)
)
insert into HoaDon
values	('HD0001','20/05/2021','minhieu'),
		('HD0002','19/05/2021','thanhduc')
create table CTHD
(
	MaHD char(11) foreign key (MaHD) references HoaDon(MaHD),
	MaMon varchar(11) foreign key (MaMon) references ThucDon(MaMon),
	SoLuong int not null,
	Gia int,
	primary key(MaHD,MaMon)
)
insert into CTHD
values	('HD0001','ST0001',1,25000),
		('HD0001','CF0001',2,10000),
		('HD0002','CF0002',3,12000)
--drop table admin
--drop table employe
--drop table ThucDon
--drop table Loai
go
create proc Finding @id char(11)
as begin
	set @id = @id + '%'
	select *
	from HoaDon, CTHD, ThucDon, employe
	where HoaDon.MaHD = CTHD.MaHD and CTHD.MaMon = ThucDon.MaMon and employe.UsenameEmp = HoaDon.UsenameEmp
		and (CTHD.MaHD like @id)
end
go
exec Finding 'HD0001'
go
select NgayDat, sum(SoLuong*Gia) as Tong
from HoaDon, CTHD
where HoaDon.MaHD=CTHD.MaHD
group by NgayDat
--drop proc spfind
go
create proc datefind @date1 char(11)
as begin
	set @date1 = @date1 + '%'
	select NgayDat, sum(SoLuong*Gia) as Tong
	from HoaDon, CTHD
	where HoaDon.MaHD=CTHD.MaHD and (@date1 = NgayDat)
	group by NgayDat
end
go
exec datefind '20/05/2021'
go
create proc spfind @sp char(11)
as begin
	set @sp = @sp + '%'
	select *
	from Loai, ThucDon
	where Loai.maloai = ThucDon.maloai and (@sp = MaMon)
end
go
exec spfind 'BX0001'
go
create proc spfind1 @sp1 nvarchar(50)
as begin
	set @sp1 = @sp1
	select *
	from Loai, ThucDon
	where Loai.maloai = ThucDon.maloai and (@sp1 = tenloai)
end
go
exec spfind1 N'Pha Chế'
go
create proc accfind @acc char(11)
as begin
	set @acc = @acc
	select *
	from employe
	where @acc = UsenameEmp
end
go
exec accfind 'thanhduc'
go
create proc kfind @date char(11)
as begin
	set @date = @date
	select *
	from Kho
	where @date = ngaynhap
end
go
exec kfind '20/05/2021'
go
create proc la @ten nvarchar(20)
as begin
	set @ten = @ten
	select * 
	from ThucDon,Loai
	where ThucDon.maloai = Loai.maloai and @ten = tenloai
end
go
exec la N'Nước Ép'