CREATE DATABASE RentingHouse
GO;

-- Tạo bảng Users
CREATE TABLE [dbo].[Users](
	[user_id] [int] IDENTITY(1,1) NOT NULL,
	[user_name] [nvarchar](50) NULL,
	[email] [varchar](255) NULL,
	[phonenumber] [char](10) NULL,
	[password] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[phonenumber] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- Tạo bảng Dịch vụ
CREATE TABLE [dbo].[Services](
	[service_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NULL,
	[service_name] [nvarchar](255) NOT NULL,
	[price] [float] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[service_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Services]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
GO

-- Tạo bảng Phòng
CREATE TABLE [dbo].[Rooms](
	[room_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NULL,
	[square] [float] NOT NULL,
	[price] [float] NOT NULL,
	[capacity] [int] NOT NULL,
	[status] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[room_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Rooms] ADD  DEFAULT ((0)) FOR [status]
GO
ALTER TABLE [dbo].[Rooms]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
GO
ALTER TABLE [dbo].[Rooms]  WITH CHECK ADD CHECK  (([price]>(0)))
GO
ALTER TABLE [dbo].[Rooms]  WITH CHECK ADD CHECK  (([square]>(0)))
GO

-- Tạo bảng cơ sở vật chất và dịch vụ của phòng
CREATE TABLE [dbo].[RoomFacilities](
	[room_id] [int] NOT NULL,
	[service_id] [int] NOT NULL,
	[number] [int] NOT NULL,
 CONSTRAINT [PK_Facility] PRIMARY KEY CLUSTERED 
(
	[room_id] ASC,
	[service_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[RoomFacilities]  WITH CHECK ADD FOREIGN KEY([room_id])
REFERENCES [dbo].[Rooms] ([room_id])
GO
ALTER TABLE [dbo].[RoomFacilities]  WITH CHECK ADD FOREIGN KEY([service_id])
REFERENCES [dbo].[Services] ([service_id])
GO

-- Tạo bảng Khách thuê
CREATE TABLE [dbo].[Tenants](
	[tenant_id] [int] IDENTITY(1,1) NOT NULL,
	[tenant_name] [nvarchar](255) NOT NULL,
	[id_number] [varchar](12) NOT NULL,
	[phone_number] [varchar](10) NOT NULL,
	[birth_date] [date] NULL,
PRIMARY KEY CLUSTERED 
(
	[tenant_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[phone_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
UNIQUE NONCLUSTERED 
(
	[id_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- Tạo bảng Hợp đồng thuê nhà
CREATE TABLE [dbo].[Contracts](
	[contract_id] [int] IDENTITY(1,1) NOT NULL,
	[user_id] [int] NULL,
	[tenant_id] [int] NULL,
	[room_id] [int] NULL,
	[start_date] [date] NULL,
	[end_date] [date] NULL,
	[deposit_amount] [float] NOT NULL,
	[status] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[contract_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Contracts] ADD  DEFAULT (getdate()) FOR [start_date]
GO
ALTER TABLE [dbo].[Contracts] ADD  DEFAULT (dateadd(month,(3),getdate())) FOR [end_date]
GO
ALTER TABLE [dbo].[Contracts] ADD  DEFAULT ((1)) FOR [status]
GO
ALTER TABLE [dbo].[Contracts]  WITH CHECK ADD FOREIGN KEY([room_id])
REFERENCES [dbo].[Rooms] ([room_id])
GO
ALTER TABLE [dbo].[Contracts]  WITH CHECK ADD FOREIGN KEY([tenant_id])
REFERENCES [dbo].[Tenants] ([tenant_id])
GO
ALTER TABLE [dbo].[Contracts]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
GO

-- Tạo bảng Biên bản hủy hợp đồng
CREATE TABLE [dbo].[Cancelation](
	[cancelation_id] [int] IDENTITY(1,1) NOT NULL,
	[contract_id] [int] NULL,
	[create_date] [date] NOT NULL,
	[party] [bit] NOT NULL,
	[compen_amount] [float] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[cancelation_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Cancelation]  WITH CHECK ADD FOREIGN KEY([contract_id])
REFERENCES [dbo].[Contracts] ([contract_id])
GO

-- Tạo bảng Hóa Đơn
CREATE TABLE [dbo].[Invoices](
	[invoice_id] [varchar](30) NOT NULL,
	[user_id] [int] NULL,
	[room_id] [int] NULL,
	[tenant_id] [int] NULL,
	[month_year] [date] NOT NULL,
	[total_amount] [float] NULL,
PRIMARY KEY CLUSTERED 
(
	[invoice_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Invoices]  WITH CHECK ADD FOREIGN KEY([room_id])
REFERENCES [dbo].[Rooms] ([room_id])
GO
ALTER TABLE [dbo].[Invoices]  WITH CHECK ADD FOREIGN KEY([tenant_id])
REFERENCES [dbo].[Tenants] ([tenant_id])
GO
ALTER TABLE [dbo].[Invoices]  WITH CHECK ADD FOREIGN KEY([user_id])
REFERENCES [dbo].[Users] ([user_id])
GO

-- Tạo bảng chi tiết hóa đơn
CREATE TABLE [dbo].[DetailInvoice](
	[invoice_id] [varchar](30) NOT NULL,
	[service_id] [int] NOT NULL,
	[number] [float] NOT NULL,
	[total_price] [float] NULL,
 CONSTRAINT [PK_DetailInvoice] PRIMARY KEY CLUSTERED 
(
	[invoice_id] ASC,
	[service_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[DetailInvoice]  WITH CHECK ADD FOREIGN KEY([invoice_id])
REFERENCES [dbo].[Invoices] ([invoice_id])
GO
ALTER TABLE [dbo].[DetailInvoice]  WITH CHECK ADD FOREIGN KEY([service_id])
REFERENCES [dbo].[Services] ([service_id])
GO

-- Tạo bảng Biên bản đền bù cơ sở vật chất
CREATE TABLE [dbo].[Compensation](
	[compensation_id] [int] IDENTITY(1,1) NOT NULL,
	[room_id] [int] NULL,
	[create_date] [date] NOT NULL,
	[total_amount] [float] NULL,
PRIMARY KEY CLUSTERED 
(
	[compensation_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Compensation]  WITH CHECK ADD FOREIGN KEY([room_id])
REFERENCES [dbo].[Rooms] ([room_id])
GO

-- Tạo bảng Chi tiết biên bản đền bù
CREATE TABLE [dbo].[DetailCompensation](
	[compensation_id] [int] NOT NULL,
	[service_id] [int] NOT NULL,
	[number] [int] NOT NULL,
	[total_price] [float] NULL,
 CONSTRAINT [PK_DetailCompensation] PRIMARY KEY CLUSTERED 
(
	[compensation_id] ASC,
	[service_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[DetailCompensation]  WITH CHECK ADD FOREIGN KEY([compensation_id])
REFERENCES [dbo].[Compensation] ([compensation_id])
GO
ALTER TABLE [dbo].[DetailCompensation]  WITH CHECK ADD FOREIGN KEY([service_id])
REFERENCES [dbo].[Services] ([service_id])
GO

-- Thêm Dữ liệu vào các bảng 


-- Trigger để cập nhật trạng thái phòng khi hủy hợp đồng
CREATE TRIGGER trg_UpdateContractAndRoomStatus
ON Contracts
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Cập nhật status của hợp đồng hết hạn
    UPDATE c
    SET c.status = 0
    FROM Contracts c
    INNER JOIN inserted i ON c.contract_id = i.contract_id
    WHERE c.end_date < CAST(GETDATE() AS DATE);

    -- Cập nhật status của phòng tương ứng nếu hợp đồng hết hạn
    UPDATE r
    SET r.status = 0
    FROM Rooms r
    INNER JOIN Contracts c ON r.room_id = c.room_id
    INNER JOIN inserted i ON c.contract_id = i.contract_id
    WHERE c.end_date < CAST(GETDATE() AS DATE);
END;



