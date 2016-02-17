USE [master]
GO
/****** Object:  Database [pypdb]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE DATABASE [pypdb]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'PenYourPrayerDB', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.MSSQLSERVER12\MSSQL\DATA\PenYourPrayerDB.mdf' , SIZE = 1729536KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'PenYourPrayerDB_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.MSSQLSERVER12\MSSQL\DATA\PenYourPrayerDB_log.ldf' , SIZE = 7460992KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [pypdb] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [pypdb].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [pypdb] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [pypdb] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [pypdb] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [pypdb] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [pypdb] SET ARITHABORT OFF 
GO
ALTER DATABASE [pypdb] SET AUTO_CLOSE ON 
GO
ALTER DATABASE [pypdb] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [pypdb] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [pypdb] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [pypdb] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [pypdb] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [pypdb] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [pypdb] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [pypdb] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [pypdb] SET  DISABLE_BROKER 
GO
ALTER DATABASE [pypdb] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [pypdb] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [pypdb] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [pypdb] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [pypdb] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [pypdb] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [pypdb] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [pypdb] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [pypdb] SET  MULTI_USER 
GO
ALTER DATABASE [pypdb] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [pypdb] SET DB_CHAINING OFF 
GO
ALTER DATABASE [pypdb] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [pypdb] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [pypdb] SET DELAYED_DURABILITY = DISABLED 
GO
USE [pypdb]
GO
/****** Object:  User [pypdbuser]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE USER [pypdbuser] WITHOUT LOGIN WITH DEFAULT_SCHEMA=[dbo]
GO
ALTER ROLE [db_owner] ADD MEMBER [pypdbuser]
GO
/****** Object:  UserDefinedFunction [dbo].[udf_Split]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[udf_Split] ( @String VARCHAR(MAX), @Delimiter VARCHAR(5)) 
RETURNS @Tokens table 
(
Token NVARCHAR(MAX)
) 
AS 
BEGIN
	WHILE (CHARINDEX(@Delimiter,@String)>0)
	BEGIN 
		INSERT INTO @Tokens (Token) VALUES (LTRIM(RTRIM(SUBSTRING(@String,1,CHARINDEX(@Delimiter,@String)-1))))
		SET @String = SUBSTRING(@String, CHARINDEX(@Delimiter,@String)+LEN(@Delimiter),LEN(@String))
	END
	INSERT INTO @Tokens (Token) VALUES (LTRIM(RTRIM(@String)))
	DELETE FROM @Tokens where LEN((LTRIM(RTRIM(Token)))) = 0
RETURN
END
GO
/****** Object:  Table [dbo].[tb_log]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_log](
	[content] [varchar](max) NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_QueueAction]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_QueueAction](
	[QueueActionID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[UserID] [bigint] NOT NULL,
	[GUID] [varchar](128) NOT NULL,
	[CreatedWhen] [datetime] NOT NULL,
 CONSTRAINT [PK_tb_QueueAction] PRIMARY KEY CLUSTERED 
(
	[QueueActionID] ASC,
	[UserID] ASC,
	[GUID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_used_nonce]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_used_nonce](
	[LoginType] [varchar](15) NOT NULL CONSTRAINT [DF_tb_used_nonce_UserID]  DEFAULT (''),
	[UserName] [varchar](50) NOT NULL,
	[RequestTime] [datetime] NOT NULL,
	[Nonce] [int] NOT NULL,
 CONSTRAINT [PK_tb_used_request_nonce_1] PRIMARY KEY CLUSTERED 
(
	[LoginType] ASC,
	[UserName] ASC,
	[RequestTime] ASC,
	[Nonce] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_user]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_user](
	[ID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[LoginType] [varchar](15) NOT NULL,
	[UserName] [varchar](50) NOT NULL,
	[SocialEmail] [varchar](50) NULL,
	[DisplayName] [nvarchar](200) NOT NULL,
	[ProfilePictureURL] [varchar](200) NULL CONSTRAINT [DF_tb_user_ProfilePictureURL]  DEFAULT (''),
	[Password] [varchar](50) NOT NULL CONSTRAINT [DF_tb_user_Password]  DEFAULT (''),
	[MobilePlatform] [varchar](10) NOT NULL CONSTRAINT [DF_tb_user_MobilePlatform]  DEFAULT (''),
	[PushNotificationID] [varchar](200) NOT NULL CONSTRAINT [DF_tb_user_PushNotificationID]  DEFAULT (''),
	[CreatedWhen] [datetime] NOT NULL CONSTRAINT [DF_tb_user_CreatedWhen]  DEFAULT (getutcdate()),
	[TouchedWhen] [datetime] NOT NULL CONSTRAINT [DF_tb_user_TouchedWhen]  DEFAULT (getutcdate()),
	[HMACHashKey] [varchar](128) NOT NULL,
	[EmailVerification] [bit] NOT NULL CONSTRAINT [DF_tb_user_EmailVerification]  DEFAULT ((0)),
	[Country] [varchar](50) NULL,
	[Region] [varchar](100) NULL,
	[City] [varchar](100) NULL,
 CONSTRAINT [PK_tb_user_1] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_user_friends]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tb_user_friends](
	[UserID] [bigint] NOT NULL,
	[FriendID] [bigint] NOT NULL,
 CONSTRAINT [PK_tb_user_friends] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC,
	[FriendID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[tb_user_onetimecode]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_user_onetimecode](
	[UserID] [bigint] NOT NULL,
	[Purpose] [varchar](50) NOT NULL,
	[RequestID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[ActivationCode] [varchar](100) NOT NULL,
	[CreatedWhen] [datetime] NULL,
	[Expired] [bit] NULL,
 CONSTRAINT [PK_tb_user_onetimecode] PRIMARY KEY CLUSTERED 
(
	[UserID] ASC,
	[Purpose] ASC,
	[RequestID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_user_prayer]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_user_prayer](
	[TouchedWhen] [datetime] NOT NULL CONSTRAINT [DF_tb_user_prayer_TouchedWhen]  DEFAULT (getutcdate()),
	[CreatedWhen] [datetime] NOT NULL CONSTRAINT [DF_tb_user_prayer_CreatedWhen]  DEFAULT (getutcdate()),
	[UserID] [bigint] NOT NULL,
	[PrayerID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[PrayerContent] [nvarchar](max) NOT NULL,
	[PublicView] [bit] NOT NULL CONSTRAINT [DF_tb_user_prayer_PublicView]  DEFAULT ((0)),
	[Deleted] [bit] NOT NULL CONSTRAINT [DF_tb_user_prayer_Deleted]  DEFAULT ((0)),
	[QueueActionGUID] [varchar](128) NULL,
 CONSTRAINT [PK_tb_user_prayer] PRIMARY KEY CLUSTERED 
(
	[PrayerID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_user_prayer_amen]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tb_user_prayer_amen](
	[AmenID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[PrayerID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
	[CreatedWhen] [datetime] NOT NULL CONSTRAINT [DF_tb_user_prayer_amen_CreatedWhen]  DEFAULT (getutcdate()),
	[TouchedWhen] [datetime] NOT NULL CONSTRAINT [DF_tb_user_prayer_amen_TouchedWhen]  DEFAULT (getutcdate()),
	[Deleted] [bit] NULL CONSTRAINT [DF_tb_user_prayer_amen_Deleted]  DEFAULT ((0)),
 CONSTRAINT [PK_tb_user_prayer_amen] PRIMARY KEY CLUSTERED 
(
	[AmenID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[tb_user_prayer_answered]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_user_prayer_answered](
	[AnsweredID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[PrayerID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
	[Answered] [nvarchar](max) NOT NULL,
	[CreatedWhen] [datetime] NOT NULL,
	[TouchedWhen] [datetime] NOT NULL,
	[Deleted] [bit] NOT NULL CONSTRAINT [DF_tb_user_prayer_answered_Deleted]  DEFAULT ((0)),
	[QueueActionGUID] [varchar](128) NOT NULL,
 CONSTRAINT [PK_tb_user_prayer_answered] PRIMARY KEY CLUSTERED 
(
	[AnsweredID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_user_prayer_attachment]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_user_prayer_attachment](
	[AttachmentID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[PrayerID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
	[FileName] [varchar](128) NOT NULL,
	[OriginalFilePath] [varchar](max) NOT NULL,
 CONSTRAINT [PK_tb_user_prayer_attachment_1] PRIMARY KEY CLUSTERED 
(
	[AttachmentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_user_prayer_comment]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tb_user_prayer_comment](
	[CommentID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[PrayerID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
	[Comment] [nvarchar](max) NOT NULL,
	[CreatedWhen] [datetime] NOT NULL,
	[TouchedWhen] [datetime] NOT NULL,
	[Deleted] [bit] NOT NULL CONSTRAINT [DF_tb_user_prayer_comment_Deleted]  DEFAULT ((0)),
	[QueueActionGUID] [varchar](128) NOT NULL,
 CONSTRAINT [PK_tb_user_prayer_comment] PRIMARY KEY CLUSTERED 
(
	[CommentID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tb_user_prayer_tag_friends]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tb_user_prayer_tag_friends](
	[PrayerID] [bigint] NOT NULL,
	[UserID] [bigint] NOT NULL,
 CONSTRAINT [PK_tb_user_prayer_tag_friends] PRIMARY KEY CLUSTERED 
(
	[PrayerID] ASC,
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[tb_user_testimony]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tb_user_testimony](
	[TouchedWhen] [datetime] NOT NULL,
	[CreatedWhen] [datetime] NOT NULL,
	[UserID] [bigint] NOT NULL,
	[TestimonyID] [bigint] IDENTITY(7700000000000000000,1) NOT NULL,
	[Testimony] [nvarchar](max) NOT NULL,
 CONSTRAINT [PK_tb_user_testimony_1] PRIMARY KEY CLUSTERED 
(
	[TestimonyID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
INSERT [dbo].[tb_log] ([content]) VALUES (N'19/11/2015 11:05:28 AM content md5: 276680A3890629ADEADE9A8D6A7AECFA')
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'ANONYMOUS', N'1398965090754988674', CAST(N'2015-11-09 15:39:11.000' AS DateTime), 27242)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'ANONYMOUS', N'1997934342009068935', CAST(N'2015-11-09 15:14:52.000' AS DateTime), 18155)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'ANONYMOUS', N'20006273755638', CAST(N'2015-11-05 08:51:18.000' AS DateTime), 25216)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'ANONYMOUS', N'80911641630592', CAST(N'2015-11-05 15:14:08.000' AS DateTime), 16251)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'EMAIL', N'MAIL@PYPTESTING.COM', CAST(N'2015-11-18 04:19:17.000' AS DateTime), 32300)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'EMAIL', N'MAIL@PYPTESTING.COM', CAST(N'2015-11-19 03:02:37.000' AS DateTime), 19355)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'EMAIL', N'MAIL@PYPTESTING.COM', CAST(N'2015-11-26 06:54:47.000' AS DateTime), 3413)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'EMAIL', N'MAIL@PYPTESTING.COM', CAST(N'2016-01-05 03:55:18.000' AS DateTime), 24781)
INSERT [dbo].[tb_used_nonce] ([LoginType], [UserName], [RequestTime], [Nonce]) VALUES (N'email', N'zniter81@gmail.com', CAST(N'2015-11-01 15:31:08.000' AS DateTime), 4576)
SET IDENTITY_INSERT [dbo].[tb_user] ON 

INSERT [dbo].[tb_user] ([ID], [LoginType], [UserName], [SocialEmail], [DisplayName], [ProfilePictureURL], [Password], [MobilePlatform], [PushNotificationID], [CreatedWhen], [TouchedWhen], [HMACHashKey], [EmailVerification], [Country], [Region], [City]) VALUES (7700000000000000000, N'Email', N'mail@pyptesting.com', NULL, N'ks', NULL, N'ExvfmGtRfrNg9z1FdSu493CCLZ5x+mabB9OCYkYDLduFWids', N'Android', N'eKqLuddGUXk:APA91bGXdRpeZrxCl3GehNLpei5iosJ5dt5aQWrsDq3rkBGeI5cIJUH7OWdVYDNdIDUdRJNTKmU64UqhWrNmcmkNu1y61zXw08x-5D1Hwm36ULdbVd51vC-lDb2BQ39TAVXE4KscI-co', CAST(N'2016-01-04 06:54:22.657' AS DateTime), CAST(N'2016-01-04 06:54:22.657' AS DateTime), N'jhtfMSJ6IpRN8kn34oKl6jHdN464NeBSzDJRpdYoUeG481TH0Bl3l5C1UsHNdt6iHT1/bfY2yH4CvmA+XwOiIV5vBA0TAD75', 1, NULL, NULL, NULL)
INSERT [dbo].[tb_user] ([ID], [LoginType], [UserName], [SocialEmail], [DisplayName], [ProfilePictureURL], [Password], [MobilePlatform], [PushNotificationID], [CreatedWhen], [TouchedWhen], [HMACHashKey], [EmailVerification], [Country], [Region], [City]) VALUES (7700000000000000001, N'Email', N'dhdh@dhd.com', NULL, N'Joshua Ng', N'https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcS_xHhCaeKTrC0G6VE49WleDv7_h6i5x1MxoQndwnVUb9bPClak', N'swwa/3Ks2zKniRw4Ob4hJJ5XE/vMDWKvYlSo4KOoUtJXzhsI', N'Android', N'eKqLuddGUXk:APA91bGXdRpeZrxCl3GehNLpei5iosJ5dt5aQWrsDq3rkBGeI5cIJUH7OWdVYDNdIDUdRJNTKmU64UqhWrNmcmkNu1y61zXw08x-5D1Hwm36ULdbVd51vC-lDb2BQ39TAVXE4KscI-co', CAST(N'2016-01-04 07:05:34.427' AS DateTime), CAST(N'2016-01-04 07:05:34.427' AS DateTime), N'H3bW1LBeakLSwiOdCBTxDIPt+dOf7Qr1GdIrr0XDFTvrkvYwsxRVqC+MorImZBZLI7sa6BwCItFSbQLBy2q820dWhZT0Ek3D', 1, NULL, NULL, NULL)
INSERT [dbo].[tb_user] ([ID], [LoginType], [UserName], [SocialEmail], [DisplayName], [ProfilePictureURL], [Password], [MobilePlatform], [PushNotificationID], [CreatedWhen], [TouchedWhen], [HMACHashKey], [EmailVerification], [Country], [Region], [City]) VALUES (7700000000000000002, N'Email', N'jsdjdj@djdj.com', NULL, N'lilian choo', NULL, N'zhJ/iqzMs7G/LGd9Qf5p5dptTs8z9rupiTCpIGl5HH+Ud11H', N'Android', N'eKqLuddGUXk:APA91bGXdRpeZrxCl3GehNLpei5iosJ5dt5aQWrsDq3rkBGeI5cIJUH7OWdVYDNdIDUdRJNTKmU64UqhWrNmcmkNu1y61zXw08x-5D1Hwm36ULdbVd51vC-lDb2BQ39TAVXE4KscI-co', CAST(N'2016-01-04 07:05:58.510' AS DateTime), CAST(N'2016-01-04 07:05:58.510' AS DateTime), N'TIVwlJi1gpNo/uOP0iqUFGn3aLxGBPid1KPUxADrlDZyU/35/zOnZcoYJ/+W/PL2GAFXpCStAJkfRWi/l4MPCbV5Whi26ry8', 1, NULL, NULL, NULL)
INSERT [dbo].[tb_user] ([ID], [LoginType], [UserName], [SocialEmail], [DisplayName], [ProfilePictureURL], [Password], [MobilePlatform], [PushNotificationID], [CreatedWhen], [TouchedWhen], [HMACHashKey], [EmailVerification], [Country], [Region], [City]) VALUES (7700000000000000003, N'Email', N'hhdudhdjdhdid@shdudjd.com', NULL, N'siewlin', NULL, N'VswX6GOKJKzcdtC84MpmyH9fSENzKmyfqJ1DjkVQCUBg3bRJ', N'Android', N'eKqLuddGUXk:APA91bGXdRpeZrxCl3GehNLpei5iosJ5dt5aQWrsDq3rkBGeI5cIJUH7OWdVYDNdIDUdRJNTKmU64UqhWrNmcmkNu1y61zXw08x-5D1Hwm36ULdbVd51vC-lDb2BQ39TAVXE4KscI-co', CAST(N'2016-01-04 07:06:36.123' AS DateTime), CAST(N'2016-01-04 07:06:36.123' AS DateTime), N'Jx8MKgGkw+HsPN3X8hdlGZMSA2nc2dS5n5Ly6vfdNKVIEixzv544lydCqM/E9d8mxqGl47ca1bZ7PAFPNwGHgAZulByI9H4f', 1, NULL, NULL, NULL)
SET IDENTITY_INSERT [dbo].[tb_user] OFF
INSERT [dbo].[tb_user_friends] ([UserID], [FriendID]) VALUES (7700000000000000000, 7700000000000000001)
INSERT [dbo].[tb_user_friends] ([UserID], [FriendID]) VALUES (7700000000000000000, 7700000000000000002)
INSERT [dbo].[tb_user_friends] ([UserID], [FriendID]) VALUES (7700000000000000000, 7700000000000000003)
INSERT [dbo].[tb_user_friends] ([UserID], [FriendID]) VALUES (7700000000000000001, 7700000000000000003)
SET IDENTITY_INSERT [dbo].[tb_user_prayer] ON 

INSERT [dbo].[tb_user_prayer] ([TouchedWhen], [CreatedWhen], [UserID], [PrayerID], [PrayerContent], [PublicView], [Deleted], [QueueActionGUID]) VALUES (CAST(N'2016-01-04 07:12:00.000' AS DateTime), CAST(N'2016-01-04 07:12:00.000' AS DateTime), 7700000000000000000, 7700000000000000000, N'Such as you know what the last year old age and we will not a new year old age and we are a bit about it would like you can do not a bit about it was thinking about it to be able too many people are not a new Jersey USA today I will need anything you know what the other hand and we are a bit about it to be the other hand and we are a bit about it to be the other day I can get back from the last year so we ', 1, 0, N'087dc3c3-af12-4c37-b799-a19e0bacd420')
INSERT [dbo].[tb_user_prayer] ([TouchedWhen], [CreatedWhen], [UserID], [PrayerID], [PrayerContent], [PublicView], [Deleted], [QueueActionGUID]) VALUES (CAST(N'2016-01-04 07:30:08.000' AS DateTime), CAST(N'2016-01-04 07:30:08.000' AS DateTime), 7700000000000000000, 7700000000000000002, N'Height he is not sure I can do not have to ', 1, 0, N'a583baa1-7b68-41f5-8968-4d0bd797bc43')
INSERT [dbo].[tb_user_prayer] ([TouchedWhen], [CreatedWhen], [UserID], [PrayerID], [PrayerContent], [PublicView], [Deleted], [QueueActionGUID]) VALUES (CAST(N'2016-01-04 09:20:25.000' AS DateTime), CAST(N'2016-01-04 09:20:25.000' AS DateTime), 7700000000000000000, 7700000000000000003, N'Haydn''s!!!!!..!!!. Hdjdnd', 0, 0, N'5d0b6b3e-ba22-4520-9ac1-d5efc3698b93')
SET IDENTITY_INSERT [dbo].[tb_user_prayer] OFF
SET IDENTITY_INSERT [dbo].[tb_user_prayer_amen] ON 

INSERT [dbo].[tb_user_prayer_amen] ([AmenID], [PrayerID], [UserID], [CreatedWhen], [TouchedWhen], [Deleted]) VALUES (7700000000000000000, 7700000000000000002, 7700000000000000002, CAST(N'2016-01-04 09:33:54.277' AS DateTime), CAST(N'2016-01-04 09:33:54.277' AS DateTime), 0)
INSERT [dbo].[tb_user_prayer_amen] ([AmenID], [PrayerID], [UserID], [CreatedWhen], [TouchedWhen], [Deleted]) VALUES (7700000000000000001, 7700000000000000002, 7700000000000000003, CAST(N'2016-01-04 09:34:03.857' AS DateTime), CAST(N'2016-01-04 09:34:03.857' AS DateTime), 0)
SET IDENTITY_INSERT [dbo].[tb_user_prayer_amen] OFF
SET IDENTITY_INSERT [dbo].[tb_user_prayer_answered] ON 

INSERT [dbo].[tb_user_prayer_answered] ([AnsweredID], [PrayerID], [UserID], [Answered], [CreatedWhen], [TouchedWhen], [Deleted], [QueueActionGUID]) VALUES (7700000000000000000, 7700000000000000002, 7700000000000000000, N'huge amount for your help in your time in a ', CAST(N'2016-01-04 09:20:47.000' AS DateTime), CAST(N'2016-01-04 09:20:47.000' AS DateTime), 0, N'12f3d8ed-76a7-4106-9ad6-368a4c58823e')
SET IDENTITY_INSERT [dbo].[tb_user_prayer_answered] OFF
SET IDENTITY_INSERT [dbo].[tb_user_prayer_attachment] ON 

INSERT [dbo].[tb_user_prayer_attachment] ([AttachmentID], [PrayerID], [UserID], [FileName], [OriginalFilePath]) VALUES (7700000000000000000, 7700000000000000002, 7700000000000000000, N'sdf', N'sdf')
INSERT [dbo].[tb_user_prayer_attachment] ([AttachmentID], [PrayerID], [UserID], [FileName], [OriginalFilePath]) VALUES (7700000000000000001, 7700000000000000002, 7700000000000000000, N'fdgdfg', N'fdgfdg')
SET IDENTITY_INSERT [dbo].[tb_user_prayer_attachment] OFF
SET IDENTITY_INSERT [dbo].[tb_user_prayer_comment] ON 

INSERT [dbo].[tb_user_prayer_comment] ([CommentID], [PrayerID], [UserID], [Comment], [CreatedWhen], [TouchedWhen], [Deleted], [QueueActionGUID]) VALUES (7700000000000000000, 7700000000000000002, 7700000000000000000, N'bdjd', CAST(N'2016-01-04 09:15:55.000' AS DateTime), CAST(N'2016-01-04 09:15:55.000' AS DateTime), 0, N'b3db572b-0680-4032-aae9-3bfbd5e6861a')
INSERT [dbo].[tb_user_prayer_comment] ([CommentID], [PrayerID], [UserID], [Comment], [CreatedWhen], [TouchedWhen], [Deleted], [QueueActionGUID]) VALUES (7700000000000000001, 7700000000000000002, 7700000000000000000, N'hunch did I was thinking we will need ', CAST(N'2016-01-04 09:16:00.000' AS DateTime), CAST(N'2016-01-04 09:16:00.000' AS DateTime), 0, N'a98de87b-f14c-4ca5-84b6-902773bf528e')
SET IDENTITY_INSERT [dbo].[tb_user_prayer_comment] OFF
INSERT [dbo].[tb_user_prayer_tag_friends] ([PrayerID], [UserID]) VALUES (7700000000000000002, 7700000000000000001)
INSERT [dbo].[tb_user_prayer_tag_friends] ([PrayerID], [UserID]) VALUES (7700000000000000002, 7700000000000000002)
INSERT [dbo].[tb_user_prayer_tag_friends] ([PrayerID], [UserID]) VALUES (7700000000000000002, 7700000000000000003)
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_tb_user_2]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE UNIQUE NONCLUSTERED INDEX [IX_tb_user_2] ON [dbo].[tb_user]
(
	[LoginType] ASC,
	[UserName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_friends]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_friends] ON [dbo].[tb_user_friends]
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_tb_user_onetimecode]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_onetimecode] ON [dbo].[tb_user_onetimecode]
(
	[ActivationCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE UNIQUE NONCLUSTERED INDEX [IX_tb_user_prayer] ON [dbo].[tb_user_prayer]
(
	[UserID] ASC,
	[PrayerID] DESC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_1]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_1] ON [dbo].[tb_user_prayer]
(
	[Deleted] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
SET ANSI_PADDING ON

GO
/****** Object:  Index [IX_tb_user_prayer_2]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE UNIQUE NONCLUSTERED INDEX [IX_tb_user_prayer_2] ON [dbo].[tb_user_prayer]
(
	[UserID] ASC,
	[QueueActionGUID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_amen]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_amen] ON [dbo].[tb_user_prayer_amen]
(
	[PrayerID] ASC,
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_amen_1]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_amen_1] ON [dbo].[tb_user_prayer_amen]
(
	[PrayerID] ASC,
	[Deleted] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_answered]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_answered] ON [dbo].[tb_user_prayer_answered]
(
	[PrayerID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_answered_1]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_answered_1] ON [dbo].[tb_user_prayer_answered]
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_answered_2]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_answered_2] ON [dbo].[tb_user_prayer_answered]
(
	[Deleted] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_attachment]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_attachment] ON [dbo].[tb_user_prayer_attachment]
(
	[UserID] ASC,
	[PrayerID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_comment]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_comment] ON [dbo].[tb_user_prayer_comment]
(
	[PrayerID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_comment_1]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_comment_1] ON [dbo].[tb_user_prayer_comment]
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_comment_2]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_comment_2] ON [dbo].[tb_user_prayer_comment]
(
	[Deleted] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_prayer_tag_friends]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_prayer_tag_friends] ON [dbo].[tb_user_prayer_tag_friends]
(
	[PrayerID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_testimony]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_testimony] ON [dbo].[tb_user_testimony]
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
/****** Object:  Index [IX_tb_user_testimony_1]    Script Date: 17/02/2016 2:06:53 PM ******/
CREATE NONCLUSTERED INDEX [IX_tb_user_testimony_1] ON [dbo].[tb_user_testimony]
(
	[UserID] ASC,
	[TestimonyID] DESC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO
ALTER TABLE [dbo].[tb_QueueAction] ADD  CONSTRAINT [DF_tb_QueueAction_CreatedWhen]  DEFAULT (getutcdate()) FOR [CreatedWhen]
GO
ALTER TABLE [dbo].[tb_user_onetimecode] ADD  CONSTRAINT [DF_tb_AccountActivationCode_ActivationCode]  DEFAULT (newid()) FOR [ActivationCode]
GO
ALTER TABLE [dbo].[tb_user_onetimecode] ADD  CONSTRAINT [DF_tb_AccountActivationCode_CreatedWhen]  DEFAULT (getutcdate()) FOR [CreatedWhen]
GO
ALTER TABLE [dbo].[tb_user_onetimecode] ADD  CONSTRAINT [DF_tb_user_onetimecode_Expired]  DEFAULT ((0)) FOR [Expired]
GO
ALTER TABLE [dbo].[tb_user_testimony] ADD  CONSTRAINT [DF_tb_user_testimony_TouchedWhen]  DEFAULT (getutcdate()) FOR [TouchedWhen]
GO
ALTER TABLE [dbo].[tb_user_testimony] ADD  CONSTRAINT [DF_tb_user_testimony_CreatedWhen]  DEFAULT (getutcdate()) FOR [CreatedWhen]
GO
ALTER TABLE [dbo].[tb_QueueAction]  WITH CHECK ADD  CONSTRAINT [FK_tb_QueueAction_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_QueueAction] CHECK CONSTRAINT [FK_tb_QueueAction_tb_user]
GO
ALTER TABLE [dbo].[tb_user_friends]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_friends_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_friends] CHECK CONSTRAINT [FK_tb_user_friends_tb_user]
GO
ALTER TABLE [dbo].[tb_user_friends]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_friends_tb_user1] FOREIGN KEY([FriendID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_friends] CHECK CONSTRAINT [FK_tb_user_friends_tb_user1]
GO
ALTER TABLE [dbo].[tb_user_onetimecode]  WITH CHECK ADD  CONSTRAINT [FK_tb_AccountActivationCode_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_onetimecode] CHECK CONSTRAINT [FK_tb_AccountActivationCode_tb_user]
GO
ALTER TABLE [dbo].[tb_user_prayer]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_prayer] CHECK CONSTRAINT [FK_tb_user_prayer_tb_user]
GO
ALTER TABLE [dbo].[tb_user_prayer_amen]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_amen_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_prayer_amen] CHECK CONSTRAINT [FK_tb_user_prayer_amen_tb_user]
GO
ALTER TABLE [dbo].[tb_user_prayer_amen]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_amen_tb_user_prayer] FOREIGN KEY([PrayerID])
REFERENCES [dbo].[tb_user_prayer] ([PrayerID])
GO
ALTER TABLE [dbo].[tb_user_prayer_amen] CHECK CONSTRAINT [FK_tb_user_prayer_amen_tb_user_prayer]
GO
ALTER TABLE [dbo].[tb_user_prayer_answered]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_answered_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_prayer_answered] CHECK CONSTRAINT [FK_tb_user_prayer_answered_tb_user]
GO
ALTER TABLE [dbo].[tb_user_prayer_answered]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_answered_tb_user_prayer] FOREIGN KEY([PrayerID])
REFERENCES [dbo].[tb_user_prayer] ([PrayerID])
GO
ALTER TABLE [dbo].[tb_user_prayer_answered] CHECK CONSTRAINT [FK_tb_user_prayer_answered_tb_user_prayer]
GO
ALTER TABLE [dbo].[tb_user_prayer_attachment]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_attachment_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_prayer_attachment] CHECK CONSTRAINT [FK_tb_user_prayer_attachment_tb_user]
GO
ALTER TABLE [dbo].[tb_user_prayer_attachment]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_attachment_tb_user_prayer] FOREIGN KEY([PrayerID])
REFERENCES [dbo].[tb_user_prayer] ([PrayerID])
GO
ALTER TABLE [dbo].[tb_user_prayer_attachment] CHECK CONSTRAINT [FK_tb_user_prayer_attachment_tb_user_prayer]
GO
ALTER TABLE [dbo].[tb_user_prayer_comment]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_comment_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_prayer_comment] CHECK CONSTRAINT [FK_tb_user_prayer_comment_tb_user]
GO
ALTER TABLE [dbo].[tb_user_prayer_comment]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_comment_tb_user_prayer] FOREIGN KEY([PrayerID])
REFERENCES [dbo].[tb_user_prayer] ([PrayerID])
GO
ALTER TABLE [dbo].[tb_user_prayer_comment] CHECK CONSTRAINT [FK_tb_user_prayer_comment_tb_user_prayer]
GO
ALTER TABLE [dbo].[tb_user_prayer_tag_friends]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_tag_friends_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_prayer_tag_friends] CHECK CONSTRAINT [FK_tb_user_prayer_tag_friends_tb_user]
GO
ALTER TABLE [dbo].[tb_user_prayer_tag_friends]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_prayer_tag_friends_tb_user_prayer] FOREIGN KEY([PrayerID])
REFERENCES [dbo].[tb_user_prayer] ([PrayerID])
GO
ALTER TABLE [dbo].[tb_user_prayer_tag_friends] CHECK CONSTRAINT [FK_tb_user_prayer_tag_friends_tb_user_prayer]
GO
ALTER TABLE [dbo].[tb_user_testimony]  WITH CHECK ADD  CONSTRAINT [FK_tb_user_testimony_tb_user] FOREIGN KEY([UserID])
REFERENCES [dbo].[tb_user] ([ID])
GO
ALTER TABLE [dbo].[tb_user_testimony] CHECK CONSTRAINT [FK_tb_user_testimony_tb_user]
GO
/****** Object:  StoredProcedure [dbo].[usp_aaaaa_mustdeleteTemporaryTesting]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_aaaaa_mustdeleteTemporaryTesting] 
AS
BEGIN
	SELECT GETUTCDATE();
END

GO
/****** Object:  StoredProcedure [dbo].[usp_ActivateUserAccount]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_ActivateUserAccount] 
(@ID BIGINT, @ActivationCode VARCHAR(100), @Result VARCHAR(200) OUTPUT, @DisplayName NVARCHAR(200) OUTPUT)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	SET @DisplayName = NULL;

	SELECT @DisplayName = B.DisplayName FROM [dbo].[tb_user_onetimecode] (NOLOCK) AS A
	INNER JOIN [dbo].[tb_user] (NOLOCK) AS B ON A.UserID = B.ID AND B.EmailVerification = 0
	WHERE A.UserID = @ID AND A.ActivationCode = @ActivationCode AND A.Purpose = 'Account Activation'

	IF (@DisplayName IS NOT NULL)
	BEGIN
		SET @Result = 'OK';
		UPDATE [dbo].[tb_user] SET EmailVerification = 1 WHERE ID = @ID;
		UPDATE [dbo].[tb_user_onetimecode] SET Expired = 1 WHERE [UserID] = @ID AND ActivationCode = @ActivationCode AND Purpose = 'Account Activation'	
	END
	ELSE
	BEGIN
		SET @DisplayName = null;
		SET @Result = 'NOT EXISTS';
	END


END

GO
/****** Object:  StoredProcedure [dbo].[usp_AddLog]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddLog] 
(@LogText VARCHAR(MAx))
AS
BEGIN
	SET NOCOUNT ON;

    INSERT INTO [dbo].[tb_log](content)
	select @LogText
END

GO
/****** Object:  StoredProcedure [dbo].[usp_AddNewPrayer]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddNewPrayer] 
(@UserID BIGINT, @Prayer NVARCHAR(MAX), @CreatedWhen DateTime, @TouchedWhen DateTime, @PublicView BIT, @Friends XML, @QueueActionGUID VARCHAR(128), @PrayerID BIGINT OUTPUT)
AS
BEGIN
	
	SET NOCOUNT ON;

    INSERT INTO [dbo].[tb_user_prayer]([UserID], [PrayerContent], PublicView, CreatedWhen, TouchedWhen, QueueActionGUID)
	SELECT @UserID, @Prayer, @PublicView, @CreatedWhen, @TouchedWhen, @QueueActionGUID;

	SET @PrayerID = Scope_Identity();
	
	INSERT INTO [dbo].[tb_user_prayer_tag_friends](PrayerID, UserID)
	select @PrayerID, T.N.value('UserID[1]', 'bigint') as [Key]
	FROM @Friends.nodes('/Friends/*') as T(N);
END

GO
/****** Object:  StoredProcedure [dbo].[usp_AddNewPrayerAmen]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddNewPrayerAmen] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @PrayerID BIGINT, @Result VARCHAR(100) OUTPUT, @AmenID BIGINT OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @QueueActionGUID) OR
	EXISTS(SELECT 1 FROM [dbo].[tb_user_prayer_amen] (NOLOCK) WHERE UserID = @UserID AND PrayerID = @PrayerID AND Deleted = 0)
	BEGIN
		SET @Result = 'EXISTS';
	END
	ELSE
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @QueueActionGUID;

		INSERT INTO [dbo].[tb_user_prayer_amen](PrayerID, UserID)
		SELECT @PrayerID, @UserID

		SET @AmenID = Scope_Identity();

		SET @Result = 'OK';
	END
END


GO
/****** Object:  StoredProcedure [dbo].[usp_AddNewPrayerAnswered]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_AddNewPrayerAnswered] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @PrayerID BIGINT, @Answered NVARCHAR(MAX), @CreatedWhen DATETIME, @TouchedWhen DATETIME, @Result VARCHAR(100) OUTPUT, @AnsweredID BIGINT OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @QueueActionGUID)
	BEGIN
		SELECT @Result = 'EXISTS-' + CONVERT(VARCHAR(20), AnsweredID) FROM [dbo].[tb_user_prayer_answered] WHERE QueueActionGUID = @QueueActionGUID
	END
	ELSE
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @QueueActionGUID;

		INSERT INTO [dbo].[tb_user_prayer_answered](PrayerID, UserID, Answered, CreatedWhen, TouchedWhen, QueueActionGUID)
		SELECT @PrayerID, @UserID, @Answered, @CreatedWhen, @TouchedWhen, @QueueActionGUID

		SET @AnsweredID = Scope_Identity();

		SET @Result = 'OK';
	END
END


GO
/****** Object:  StoredProcedure [dbo].[usp_AddNewPrayerAttachment]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddNewPrayerAttachment]
(@PrayerID BIGINT, @Filename VARCHAR(128), @OriginalFilePath VARCHAR(MAX), @UserID BIGINT, @AttachmentID BIGINT OUTPUT)
AS
BEGIN
	IF NOT EXISTS(SELECT 1 FROM [dbo].[tb_user_prayer_attachment] (NOLOCK) WHERE PrayerID = @PrayerID AND [Filename] = @Filename)
	BEGIN
		INSERT INTO [dbo].[tb_user_prayer_attachment]([PrayerID], [Filename], [OriginalFilePath], [UserID])
		SELECT @PrayerID, @Filename, @OriginalFilePath, @UserID;

		SET @AttachmentID = Scope_Identity();
	END
	ELSE
	BEGIN
		SELECT @AttachmentID = AttachmentID FROM [dbo].[tb_user_prayer_attachment] WHERE PrayerID = @PrayerID AND [Filename] = @Filename;
	END
END

GO
/****** Object:  StoredProcedure [dbo].[usp_AddNewPrayerComment]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddNewPrayerComment] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @PrayerID BIGINT, @Comment NVARCHAR(MAX), @CreatedWhen DATETIME, @TouchedWhen DATETIME, @Result VARCHAR(100) OUTPUT, @CommentID BIGINT OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @QueueActionGUID)
	BEGIN
		SELECT @Result = 'EXISTS-' + CONVERT(VARCHAR(20), CommentID) FROM [dbo].[tb_user_prayer_comment] WHERE QueueActionGUID = @QueueActionGUID
	END
	ELSE
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @QueueActionGUID;

		INSERT INTO [dbo].[tb_user_prayer_comment](PrayerID, UserID, Comment, CreatedWhen, TouchedWhen, QueueActionGUID)
		SELECT @PrayerID, @UserID, @Comment, @CreatedWhen, @TouchedWhen, @QueueActionGUID

		SET @CommentID = Scope_Identity();

		SET @Result = 'OK';
	END
END


GO
/****** Object:  StoredProcedure [dbo].[usp_AddNewUser]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddNewUser] 
(@LoginType VARCHAR(15), @UserName VARCHAR(50), @Name NVARCHAR(200), @ProfilePictureURL VARCHAR(200), @Password VARCHAR(50),
 @MobilePlatform VARCHAR(10), @PushNotificationID VARCHAR(200), @HMACHashKey VARCHAR(128),
 @Country VARCHAR(50), @Region VARCHAR(100), @City VARCHAR(100),
 @Result VARCHAR(200) OUTPUT, @ID BIGINT OUTPUT, @VerificationCode VARCHAR(100) OUTPUT)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    IF NOT EXISTS(SELECT 1 FROM [dbo].[tb_user] (NOLOCK) WHERE LoginType = @LoginType and UserName = @UserName)
	BEGIN
		
		DECLARE @EmailVerification BIT = NULL;

		IF(@LoginType = 'Email')
		BEGIN
			SET @EmailVerification = 0;
		END

		INSERT INTO [dbo].[tb_user](LoginType, UserName, DisplayName, ProfilePictureURL, [Password], MobilePlatform, PushNotificationID, 
								    HMACHashKey, EmailVerification, Country, Region, City)
		SELECT @LoginType, @UserName, @Name, @ProfilePictureURL, @Password, @MobilePlatform, @PushNotificationID,
			   @HMACHashKey, @EmailVerification, @Country, @Region, @City;

		SELECT @ID = ID FROM [dbo].[tb_user] WHERE LoginType = @LoginType AND UserName = @UserName;


		SET @VerificationCode = REPLACE(CONVERT(VARCHAR(36), NEWID()) + '-' + CONVERT(VARCHAR(36), NEWID()) + '-' + CONVERT(VARCHAR(36), NEWID()), '-','');
		INSERT INTO [dbo].[tb_user_onetimecode]([UserID], [Purpose], ActivationCode)
		SELECT @ID, 'Account Activation', @VerificationCode

		SET @Result = 'OK';
		
	END
	ELSE
	BEGIN
		set @Result = 'EXISTS';
	END


END

GO
/****** Object:  StoredProcedure [dbo].[usp_AddNonce]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddNonce] 
(@LoginType VARCHAR(15), @Username VARCHAR(50), @RequestDate DATETIME, @Nonce INT, @Result VARCHAR(200) OUTPUT)
AS
BEGIN
	SET NOCOUNT ON;

    IF EXISTS(SELECT 1 FROM [dbo].[tb_used_nonce] (NOLOCK) WHERE LoginType = @LoginType AND UserName = @Username AND RequestTime = @RequestDate AND Nonce = @Nonce)
	BEGIN
		SET @Result = 'EXISTS';
	END
	ELSE
	BEGIN
		INSERT INTO [dbo].[tb_used_nonce](LoginType, UserName, RequestTime, Nonce)
		SELECT @LoginType, @Username, @RequestDate, @Nonce;

		SET @Result = 'OK';
	END
END

GO
/****** Object:  StoredProcedure [dbo].[usp_AddQueueAction]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_AddQueueAction] 
(@UserID BIGINT, @GUID VARCHAR(128), @Result VARCHAR(10) OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @GUID)
	BEGIN
		SET @Result = 'EXISTS';
	END
	ELSE
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @GUID;
		SET @Result = 'OK';
	END
END

GO
/****** Object:  StoredProcedure [dbo].[usp_DeletePrayer]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_DeletePrayer] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @PrayerID BIGINT)
AS
BEGIN
	UPDATE [dbo].[tb_user_prayer] SET Deleted = 1, TouchedWhen = GETUTCDATE()
	WHERE UserID = @UserID AND PrayerID = @PrayerID;
END


GO
/****** Object:  StoredProcedure [dbo].[usp_DeletePrayerAmen]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_DeletePrayerAmen] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @PrayerID BIGINT)
AS
BEGIN
	UPDATE [dbo].[tb_user_prayer_amen] SET Deleted = 1, TouchedWhen = GETUTCDATE()
	WHERE UserID = @UserID AND PrayerID = @PrayerID;
END


GO
/****** Object:  StoredProcedure [dbo].[usp_DeletePrayerComment]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_DeletePrayerComment] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @CommentID BIGINT, @Result VARCHAR(100) OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @QueueActionGUID)
	BEGIN
		SET @Result = 'EXISTS';
	END
	ELSE
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @QueueActionGUID;

		UPDATE [dbo].[tb_user_prayer_comment] SET TouchedWhen = GETUTCDATE(), Deleted = 1 WHERE [CommentID] = @CommentID;

		SET @Result = 'OK';
	END
END


GO
/****** Object:  StoredProcedure [dbo].[usp_GetCreatedPrayerFromQueueActionGUID]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetCreatedPrayerFromQueueActionGUID] 
(@UserID BIGINT, @QueueActionGUID VARCHAR(128), @PrayerID BIGINT OUTPUT)
AS
BEGIN
	
	SET NOCOUNT ON;
    SELECT @PrayerID = PrayerID FROM [dbo].[tb_user_prayer] (NOLOCK) WHERE UserID = @UserID AND QueueActionGUID = @QueueActionGUID
END


GO
/****** Object:  StoredProcedure [dbo].[usp_GetLatestFriends]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_GetLatestFriends] 
(@UserID BIGINT, @friends VARCHAR(MAX))
AS
BEGIN
	
	DECLARE @TABLE TABLE(UserID BIGINT)

	INSERT INTO @TABLE
	SELECT Token from [dbo].[udf_Split](@friends, ';')

	SELECT B.DisplayName, B.ProfilePictureURL, B.ID FROM [dbo].[tb_user_friends] AS A 
	INNER JOIN [dbo].[tb_user] AS B ON B.ID = A.FriendID
	WHERE A.UserID = @UserID AND A.[FriendID] NOT IN(SELECT UserID from @TABLE)
END

GO
/****** Object:  StoredProcedure [dbo].[usp_GetLatestPrayers]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_GetLatestPrayers] 
(@UserID BIGINT, @PrayerID BIGINT)
AS
BEGIN
	SELECT [TouchedWhen] ,[CreatedWhen] ,[UserID] ,[PrayerID] ,[PrayerContent] ,[PublicView] ,[QueueActionGUID]
	       ,CONVERT(XML, (SELECT [UserID], G.[DisplayName], G.[ProfilePictureURL] FROM [dbo].[tb_user_prayer_tag_friends] AS F (NOLOCK) INNER JOIN [dbo].[tb_user] AS G (NOLOCK) ON G.ID = F.UserID WHERE F.PrayerID = A.PrayerID FOR XML PATH('TagFriend'), ROOT('AllTagFriends'))) AS TagFriends
		   ,CONVERT(XML ,(SELECT [AttachmentID] AS [GUID], B.UserID ,[FileName] ,[OriginalFilePath] FROM [dbo].[tb_user_prayer_attachment] AS B (NOLOCK) WHERE B.PrayerID = A.PrayerID FOR XML PATH('Attachment'), ROOT('AllAttachments'))) AS Attachments
		   ,CONVERT(XML ,(SELECT [CommentID], [UserID] AS WhoID, H.DisplayName AS WhoName, H.ProfilePictureURL AS WhoProfilePicture, [Comment] ,C.[CreatedWhen] ,C.[TouchedWhen] FROM [dbo].[tb_user_prayer_comment] AS C (NOLOCK) INNER JOIN [dbo].[tb_user] AS H (NOLOCK) ON H.ID = C.UserID WHERE C.Deleted = 0 AND C.PrayerID = A.PrayerID AND C.UserID = A.UserID FOR XML PATH('Comment'), ROOT('AllComments'))) AS Comment
		   ,Convert(XML ,(SELECT [AnsweredID] ,[Answered] ,[CreatedWhen] ,[TouchedWhen] FROM [dbo].[tb_user_prayer_answered] AS D (NOLOCK) WHERE Deleted = 0 AND D.PrayerID = A.PrayerID AND D.UserID = A.UserID FOR XML PATH('Answer'), ROOT('AllAnswers'))) AS Answers
		   ,CONVERT(XML, (SELECT [AmenID] ,[UserID] AS WhoID ,I.DisplayName AS WhoName, I.ProfilePictureURL AS WhoProfilePicture, E.[CreatedWhen] , E.[TouchedWhen] FROM [dbo].[tb_user_prayer_amen] AS E (NOLOCK) INNER JOIN [dbo].[tb_user] AS I (NOLOCK) ON I.ID = E.UserID WHERE E.Deleted = 0 AND E.PrayerID = A.PrayerID FOR XML PATH('Amen'), ROOT('AllAmen'))) AS Amen		   
	FROM [dbo].[tb_user_prayer] As A (NOLOCK)
	WHERE A.[UserID] = @UserID AND A.[PrayerID] > @PrayerID AND Deleted = 0
	ORDER BY [PrayerID]

	-- name xml name must be the same as the android object name.
	-- put no lock.
END

GO
/****** Object:  StoredProcedure [dbo].[usp_GetPrayerAttachmentInformation]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_GetPrayerAttachmentInformation]
(@AttachmentID BIGINT, @UserID BIGINT, @Filename VARCHAR(128) OUTPUT, @OwnerID BIGINT OUTPUT)
AS
BEGIN

	if EXISTS(SELECT 1 FROM [tb_user_prayer_attachment] WHERE @AttachmentID = AttachmentID AND @UserID = UserID) 
		OR EXISTS(SELECT 1 FROM [dbo].[tb_user_prayer_tag_friends] WHERE PrayerID = (SELECT PrayerID FROM [tb_user_prayer_attachment] WHERE @AttachmentID = AttachmentID) AND UserID = @UserID)
	BEGIN
		
		SELECT @Filename = [FileName], @OwnerID = UserID FROM [tb_user_prayer_attachment] WHERE @AttachmentID = AttachmentID

	END
  
  

END

GO
/****** Object:  StoredProcedure [dbo].[usp_GetUserActivationCode]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[usp_GetUserActivationCode] 
(@LoginType VARCHAR(15), @UserName VARCHAR(50))
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT LoginType, UserName, A.DisplayName, A.ID, B.ActivationCode FROM [dbo].[tb_user] (NOLOCK) AS A
	INNER JOIN [dbo].[tb_user_onetimecode] (NOLOCK) AS B ON B.UserID = A.ID AND B.[Purpose] = 'Account Activation'
	WHERE B.Expired = 0 AND LoginType = @LoginType AND UserName = @UserName AND [EmailVerification] = 0;

END

GO
/****** Object:  StoredProcedure [dbo].[usp_GetUserInformation]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_GetUserInformation] 
(@LoginType VARCHAR(15), @UserName VARCHAR(50))
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT [LoginType] ,[UserName] ,[ID] ,[DisplayName] ,[ProfilePictureURL]
      ,[Password] ,[MobilePlatform] ,[PushNotificationID] ,[CreatedWhen] ,[TouchedWhen], [HMACHashKey]
	  ,[Country], [Region], [City], EmailVerification
	FROM [dbo].[tb_user] (NOLOCK) WHERE LoginType = @LoginType and UserName = @UserName 


END

GO
/****** Object:  StoredProcedure [dbo].[usp_ResetPassword]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_ResetPassword] 
(@LoginType VARCHAR(15), @Username VARCHAR(50), @Result VARCHAR(200) OUTPUT, @ID BIGINT OUTPUT, @VerificationCode VARCHAR(100) OUTPUT, @DisplayName VARCHAR(200) OUTPUT)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT @ID = ID, @DisplayName = DisplayName
	FROM [dbo].[tb_user] (NOLOCK)
	WHERE LoginType = @LoginType AND UserName = @Username;

	SET @VerificationCode = REPLACE(CONVERT(VARCHAR(36), NEWID()) + '-' + CONVERT(VARCHAR(36), NEWID()) + '-' + CONVERT(VARCHAR(36), NEWID()), '-','');			

	if(@ID IS NULL)
	BEGIN
		SET @Result = 'NOT EXISTS';
	END

    UPDATE [dbo].[tb_user_onetimecode] SET Expired = 1
	WHERE [Purpose] = 'Password Reset' AND UserID = @ID

	INSERT INTO [dbo].[tb_user_onetimecode] (UserID, Purpose, ActivationCode)
	SELECT @ID, 'Password Reset', @VerificationCode;

	SET @Result = 'OK';
END

GO
/****** Object:  StoredProcedure [dbo].[usp_UpdatePrayerComment]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_UpdatePrayerComment] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @CommentID BIGINT, @Comment NVARCHAR(MAX), @TouchedWhen DATETIME, @Result VARCHAR(100) OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @QueueActionGUID)
	BEGIN
		SET @Result = 'EXISTS';
	END
	ELSE
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @QueueActionGUID;

		UPDATE [dbo].[tb_user_prayer_comment] SET TouchedWhen = @TouchedWhen, Comment = @Comment WHERE CommentID = @CommentID

		SET @CommentID = Scope_Identity();

		SET @Result = 'OK';
	END
END


GO
/****** Object:  StoredProcedure [dbo].[usp_UpdatePrayerPublicView]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_UpdatePrayerPublicView] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @PrayerID BIGINT, @PublicView BIT, @Result VARCHAR(100) OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @QueueActionGUID)
	BEGIN
		SET @Result = 'EXISTS';
	END
	ELSE IF EXISTS(SELECT 1 FROM [dbo].[tb_user_prayer] WHERE PrayerID = @PrayerID)
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @QueueActionGUID;

		UPDATE [dbo].[tb_user_prayer] SET TouchedWhen = GETUTCDATE(), PublicView = @PublicView WHERE PrayerID = @PrayerID;
		SET @Result = 'OK';
	END
	ELSE
	BEGIN
		SET @Result = 'NOTEXISTS';
	END
END


GO
/****** Object:  StoredProcedure [dbo].[usp_UpdatePrayerTagFriends]    Script Date: 17/02/2016 2:06:53 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[usp_UpdatePrayerTagFriends] 
(@QueueActionGUID VARCHAR(128), @UserID BIGINT, @PrayerID BIGINT, @Friends XML, @Result VARCHAR(100) OUTPUT)
AS
BEGIN
	IF EXISTS(SELECT 1 FROM [dbo].[tb_QueueAction] (NOLOCK) WHERE [UserID] = @UserID AND [GUID] = @QueueActionGUID)
	BEGIN
		SET @Result = 'EXISTS';
	END
	ELSE IF EXISTS(SELECT 1 FROM [dbo].[tb_user_prayer] WHERE PrayerID = @PrayerID)
	BEGIN
		INSERT INTO [dbo].[tb_QueueAction](UserID, [GUID])
		SELECT @UserID, @QueueActionGUID;

		DELETE FROM [dbo].[tb_user_prayer_tag_friends] WHERE PrayerID = @PrayerID

		INSERT INTO [dbo].[tb_user_prayer_tag_friends](PrayerID, UserID)
		select @PrayerID, T.N.value('UserID[1]', 'bigint') as [Key]
		FROM @Friends.nodes('/Friends/*') as T(N);

		UPDATE [dbo].[tb_user_prayer] SET TouchedWhen = GETUTCDATE() WHERE PrayerID = @PrayerID;
		SET @Result = 'OK';
	END
	ELSE
	BEGIN
		SET @Result = 'NOTEXISTS';
	END
END


GO
USE [master]
GO
ALTER DATABASE [pypdb] SET  READ_WRITE 
GO
