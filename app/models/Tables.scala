package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(AsciiflowBoard.schema, BlokGroup.schema, BloksSchool.schema, BloksUser.schema, Contact.schema, Friend.schema, GroupMembership.schema, GroupThread.schema, Message.schema, Notification.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table AsciiflowBoard
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param school Database column school SqlType(int4)
   *  @param groupId Database column group_id SqlType(int4)
   *  @param board Database column board SqlType(text) */
  case class AsciiflowBoardRow(id: Int, school: Int, groupId: Int, board: String)
  /** GetResult implicit for fetching AsciiflowBoardRow objects using plain SQL queries */
  implicit def GetResultAsciiflowBoardRow(implicit e0: GR[Int], e1: GR[String]): GR[AsciiflowBoardRow] = GR{
    prs => import prs._
    AsciiflowBoardRow.tupled((<<[Int], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table asciiflow_board. Objects of this class serve as prototypes for rows in queries. */
  class AsciiflowBoard(_tableTag: Tag) extends profile.api.Table[AsciiflowBoardRow](_tableTag, "asciiflow_board") {
    def * = (id, school, groupId, board) <> (AsciiflowBoardRow.tupled, AsciiflowBoardRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(school), Rep.Some(groupId), Rep.Some(board))).shaped.<>({r=>import r._; _1.map(_=> AsciiflowBoardRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column school SqlType(int4) */
    val school: Rep[Int] = column[Int]("school")
    /** Database column group_id SqlType(int4) */
    val groupId: Rep[Int] = column[Int]("group_id")
    /** Database column board SqlType(text) */
    val board: Rep[String] = column[String]("board")
  }
  /** Collection-like TableQuery object for table AsciiflowBoard */
  lazy val AsciiflowBoard = new TableQuery(tag => new AsciiflowBoard(tag))

  /** Entity class storing rows of table BlokGroup
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param school Database column school SqlType(int4)
   *  @param name Database column name SqlType(varchar), Length(200,true)
   *  @param `type` Database column type SqlType(int2)
   *  @param icon Database column icon SqlType(varchar), Length(650,true) */
  case class BlokGroupRow(id: Int, school: Int, name: String, `type`: Short, icon: String)
  /** GetResult implicit for fetching BlokGroupRow objects using plain SQL queries */
  implicit def GetResultBlokGroupRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Short]): GR[BlokGroupRow] = GR{
    prs => import prs._
    BlokGroupRow.tupled((<<[Int], <<[Int], <<[String], <<[Short], <<[String]))
  }
  /** Table description of table blok_group. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class BlokGroup(_tableTag: Tag) extends profile.api.Table[BlokGroupRow](_tableTag, "blok_group") {
    def * = (id, school, name, `type`, icon) <> (BlokGroupRow.tupled, BlokGroupRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(school), Rep.Some(name), Rep.Some(`type`), Rep.Some(icon))).shaped.<>({r=>import r._; _1.map(_=> BlokGroupRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column school SqlType(int4) */
    val school: Rep[Int] = column[Int]("school")
    /** Database column name SqlType(varchar), Length(200,true) */
    val name: Rep[String] = column[String]("name", O.Length(200,varying=true))
    /** Database column type SqlType(int2)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Short] = column[Short]("type")
    /** Database column icon SqlType(varchar), Length(650,true) */
    val icon: Rep[String] = column[String]("icon", O.Length(650,varying=true))
  }
  /** Collection-like TableQuery object for table BlokGroup */
  lazy val BlokGroup = new TableQuery(tag => new BlokGroup(tag))

  /** Entity class storing rows of table BloksSchool
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(150,true)
   *  @param emailExtension Database column email_extension SqlType(varchar), Length(150,true)
   *  @param population Database column population SqlType(int2)
   *  @param state Database column state SqlType(varchar), Length(85,true)
   *  @param schoolRequirements Database column school_requirements SqlType(int2)
   *  @param schoolFunding Database column school_funding SqlType(int2)
   *  @param dateOfRegistration Database column date_of_registration SqlType(date) */
  case class BloksSchoolRow(id: Int, name: String, emailExtension: String, population: Short, state: String, schoolRequirements: Short, schoolFunding: Short, dateOfRegistration: java.sql.Date)
  /** GetResult implicit for fetching BloksSchoolRow objects using plain SQL queries */
  implicit def GetResultBloksSchoolRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Short], e3: GR[java.sql.Date]): GR[BloksSchoolRow] = GR{
    prs => import prs._
    BloksSchoolRow.tupled((<<[Int], <<[String], <<[String], <<[Short], <<[String], <<[Short], <<[Short], <<[java.sql.Date]))
  }
  /** Table description of table bloks_school. Objects of this class serve as prototypes for rows in queries. */
  class BloksSchool(_tableTag: Tag) extends profile.api.Table[BloksSchoolRow](_tableTag, "bloks_school") {
    def * = (id, name, emailExtension, population, state, schoolRequirements, schoolFunding, dateOfRegistration) <> (BloksSchoolRow.tupled, BloksSchoolRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(name), Rep.Some(emailExtension), Rep.Some(population), Rep.Some(state), Rep.Some(schoolRequirements), Rep.Some(schoolFunding), Rep.Some(dateOfRegistration))).shaped.<>({r=>import r._; _1.map(_=> BloksSchoolRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(150,true) */
    val name: Rep[String] = column[String]("name", O.Length(150,varying=true))
    /** Database column email_extension SqlType(varchar), Length(150,true) */
    val emailExtension: Rep[String] = column[String]("email_extension", O.Length(150,varying=true))
    /** Database column population SqlType(int2) */
    val population: Rep[Short] = column[Short]("population")
    /** Database column state SqlType(varchar), Length(85,true) */
    val state: Rep[String] = column[String]("state", O.Length(85,varying=true))
    /** Database column school_requirements SqlType(int2) */
    val schoolRequirements: Rep[Short] = column[Short]("school_requirements")
    /** Database column school_funding SqlType(int2) */
    val schoolFunding: Rep[Short] = column[Short]("school_funding")
    /** Database column date_of_registration SqlType(date) */
    val dateOfRegistration: Rep[java.sql.Date] = column[java.sql.Date]("date_of_registration")
  }
  /** Collection-like TableQuery object for table BloksSchool */
  lazy val BloksSchool = new TableQuery(tag => new BloksSchool(tag))

  /** Entity class storing rows of table BloksUser
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param blokId Database column blok_id SqlType(int4)
   *  @param isValidated Database column is_validated SqlType(bool)
   *  @param name Database column name SqlType(varchar), Length(150,true)
   *  @param email Database column email SqlType(varchar), Length(150,true)
   *  @param electedRank Database column elected_rank SqlType(int2)
   *  @param password Database column password SqlType(varchar), Length(100,true)
   *  @param grade Database column grade SqlType(int4)
   *  @param relationshipStatus Database column relationship_status SqlType(varchar), Length(100,true)
   *  @param gender Database column gender SqlType(varchar), Length(50,true)
   *  @param showGender Database column show_gender SqlType(bool)
   *  @param biologicalSex Database column biological_sex SqlType(varchar), Length(50,true)
   *  @param showBiologicalSex Database column show_biological_sex SqlType(bool)
   *  @param biography Database column biography SqlType(varchar), Length(1000,true)
   *  @param dateOfRegistration Database column date_of_registration SqlType(date)
   *  @param profilePic Database column profile_pic SqlType(varchar), Length(650,true) */
  case class BloksUserRow(id: Int, blokId: Int, isValidated: Boolean, name: String, email: String, electedRank: Short, password: String, grade: Int, relationshipStatus: String, gender: String, showGender: Boolean, biologicalSex: String, showBiologicalSex: Boolean, biography: String, dateOfRegistration: java.sql.Date, profilePic: String)
  /** GetResult implicit for fetching BloksUserRow objects using plain SQL queries */
  implicit def GetResultBloksUserRow(implicit e0: GR[Int], e1: GR[Boolean], e2: GR[String], e3: GR[Short], e4: GR[java.sql.Date]): GR[BloksUserRow] = GR{
    prs => import prs._
    BloksUserRow.tupled((<<[Int], <<[Int], <<[Boolean], <<[String], <<[String], <<[Short], <<[String], <<[Int], <<[String], <<[String], <<[Boolean], <<[String], <<[Boolean], <<[String], <<[java.sql.Date], <<[String]))
  }
  /** Table description of table bloks_user. Objects of this class serve as prototypes for rows in queries. */
  class BloksUser(_tableTag: Tag) extends profile.api.Table[BloksUserRow](_tableTag, "bloks_user") {
    def * = (id, blokId, isValidated, name, email, electedRank, password, grade, relationshipStatus, gender, showGender, biologicalSex, showBiologicalSex, biography, dateOfRegistration, profilePic) <> (BloksUserRow.tupled, BloksUserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(blokId), Rep.Some(isValidated), Rep.Some(name), Rep.Some(email), Rep.Some(electedRank), Rep.Some(password), Rep.Some(grade), Rep.Some(relationshipStatus), Rep.Some(gender), Rep.Some(showGender), Rep.Some(biologicalSex), Rep.Some(showBiologicalSex), Rep.Some(biography), Rep.Some(dateOfRegistration), Rep.Some(profilePic))).shaped.<>({r=>import r._; _1.map(_=> BloksUserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column blok_id SqlType(int4) */
    val blokId: Rep[Int] = column[Int]("blok_id")
    /** Database column is_validated SqlType(bool) */
    val isValidated: Rep[Boolean] = column[Boolean]("is_validated")
    /** Database column name SqlType(varchar), Length(150,true) */
    val name: Rep[String] = column[String]("name", O.Length(150,varying=true))
    /** Database column email SqlType(varchar), Length(150,true) */
    val email: Rep[String] = column[String]("email", O.Length(150,varying=true))
    /** Database column elected_rank SqlType(int2) */
    val electedRank: Rep[Short] = column[Short]("elected_rank")
    /** Database column password SqlType(varchar), Length(100,true) */
    val password: Rep[String] = column[String]("password", O.Length(100,varying=true))
    /** Database column grade SqlType(int4) */
    val grade: Rep[Int] = column[Int]("grade")
    /** Database column relationship_status SqlType(varchar), Length(100,true) */
    val relationshipStatus: Rep[String] = column[String]("relationship_status", O.Length(100,varying=true))
    /** Database column gender SqlType(varchar), Length(50,true) */
    val gender: Rep[String] = column[String]("gender", O.Length(50,varying=true))
    /** Database column show_gender SqlType(bool) */
    val showGender: Rep[Boolean] = column[Boolean]("show_gender")
    /** Database column biological_sex SqlType(varchar), Length(50,true) */
    val biologicalSex: Rep[String] = column[String]("biological_sex", O.Length(50,varying=true))
    /** Database column show_biological_sex SqlType(bool) */
    val showBiologicalSex: Rep[Boolean] = column[Boolean]("show_biological_sex")
    /** Database column biography SqlType(varchar), Length(1000,true) */
    val biography: Rep[String] = column[String]("biography", O.Length(1000,varying=true))
    /** Database column date_of_registration SqlType(date) */
    val dateOfRegistration: Rep[java.sql.Date] = column[java.sql.Date]("date_of_registration")
    /** Database column profile_pic SqlType(varchar), Length(650,true) */
    val profilePic: Rep[String] = column[String]("profile_pic", O.Length(650,varying=true))
  }
  /** Collection-like TableQuery object for table BloksUser */
  lazy val BloksUser = new TableQuery(tag => new BloksUser(tag))

  /** Entity class storing rows of table Contact
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(int4)
   *  @param contactUserId Database column contact_user_id SqlType(int4)
   *  @param contactName Database column contact_name SqlType(varchar), Length(150,true)
   *  @param contactProfilePic Database column contact_profile_pic SqlType(varchar), Length(650,true)
   *  @param contactElectedRank Database column contact_elected_rank SqlType(int2)
   *  @param contactGrade Database column contact_grade SqlType(int4) */
  case class ContactRow(id: Int, userId: Int, contactUserId: Int, contactName: String, contactProfilePic: String, contactElectedRank: Short, contactGrade: Int)
  /** GetResult implicit for fetching ContactRow objects using plain SQL queries */
  implicit def GetResultContactRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Short]): GR[ContactRow] = GR{
    prs => import prs._
    ContactRow.tupled((<<[Int], <<[Int], <<[Int], <<[String], <<[String], <<[Short], <<[Int]))
  }
  /** Table description of table contact. Objects of this class serve as prototypes for rows in queries. */
  class Contact(_tableTag: Tag) extends profile.api.Table[ContactRow](_tableTag, "contact") {
    def * = (id, userId, contactUserId, contactName, contactProfilePic, contactElectedRank, contactGrade) <> (ContactRow.tupled, ContactRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(userId), Rep.Some(contactUserId), Rep.Some(contactName), Rep.Some(contactProfilePic), Rep.Some(contactElectedRank), Rep.Some(contactGrade))).shaped.<>({r=>import r._; _1.map(_=> ContactRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column contact_user_id SqlType(int4) */
    val contactUserId: Rep[Int] = column[Int]("contact_user_id")
    /** Database column contact_name SqlType(varchar), Length(150,true) */
    val contactName: Rep[String] = column[String]("contact_name", O.Length(150,varying=true))
    /** Database column contact_profile_pic SqlType(varchar), Length(650,true) */
    val contactProfilePic: Rep[String] = column[String]("contact_profile_pic", O.Length(650,varying=true))
    /** Database column contact_elected_rank SqlType(int2) */
    val contactElectedRank: Rep[Short] = column[Short]("contact_elected_rank")
    /** Database column contact_grade SqlType(int4) */
    val contactGrade: Rep[Int] = column[Int]("contact_grade")
  }
  /** Collection-like TableQuery object for table Contact */
  lazy val Contact = new TableQuery(tag => new Contact(tag))

  /** Entity class storing rows of table Friend
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param fromId Database column from_id SqlType(int4)
   *  @param toId Database column to_id SqlType(int4) */
  case class FriendRow(id: Int, fromId: Int, toId: Int)
  /** GetResult implicit for fetching FriendRow objects using plain SQL queries */
  implicit def GetResultFriendRow(implicit e0: GR[Int]): GR[FriendRow] = GR{
    prs => import prs._
    FriendRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table friend. Objects of this class serve as prototypes for rows in queries. */
  class Friend(_tableTag: Tag) extends profile.api.Table[FriendRow](_tableTag, "friend") {
    def * = (id, fromId, toId) <> (FriendRow.tupled, FriendRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(fromId), Rep.Some(toId))).shaped.<>({r=>import r._; _1.map(_=> FriendRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column from_id SqlType(int4) */
    val fromId: Rep[Int] = column[Int]("from_id")
    /** Database column to_id SqlType(int4) */
    val toId: Rep[Int] = column[Int]("to_id")
  }
  /** Collection-like TableQuery object for table Friend */
  lazy val Friend = new TableQuery(tag => new Friend(tag))

  /** Entity class storing rows of table GroupMembership
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param groupId Database column group_id SqlType(int4)
   *  @param userId Database column user_id SqlType(int4)
   *  @param role Database column role SqlType(int2) */
  case class GroupMembershipRow(id: Int, groupId: Int, userId: Int, role: Short)
  /** GetResult implicit for fetching GroupMembershipRow objects using plain SQL queries */
  implicit def GetResultGroupMembershipRow(implicit e0: GR[Int], e1: GR[Short]): GR[GroupMembershipRow] = GR{
    prs => import prs._
    GroupMembershipRow.tupled((<<[Int], <<[Int], <<[Int], <<[Short]))
  }
  /** Table description of table group_membership. Objects of this class serve as prototypes for rows in queries. */
  class GroupMembership(_tableTag: Tag) extends profile.api.Table[GroupMembershipRow](_tableTag, "group_membership") {
    def * = (id, groupId, userId, role) <> (GroupMembershipRow.tupled, GroupMembershipRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(groupId), Rep.Some(userId), Rep.Some(role))).shaped.<>({r=>import r._; _1.map(_=> GroupMembershipRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column group_id SqlType(int4) */
    val groupId: Rep[Int] = column[Int]("group_id")
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column role SqlType(int2) */
    val role: Rep[Short] = column[Short]("role")
  }
  /** Collection-like TableQuery object for table GroupMembership */
  lazy val GroupMembership = new TableQuery(tag => new GroupMembership(tag))

  /** Entity class storing rows of table GroupThread
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param groupId Database column group_id SqlType(int4)
   *  @param userId Database column user_id SqlType(int4)
   *  @param rootThread Database column root_thread SqlType(int4)
   *  @param threadTitle Database column thread_title SqlType(varchar), Length(200,true)
   *  @param thread Database column thread SqlType(text)
   *  @param threadDate Database column thread_date SqlType(timestamp with time zone) */
  case class GroupThreadRow(id: Int, groupId: Int, userId: Int, rootThread: Int, threadTitle: String, thread: String, threadDate: java.sql.Timestamp)
  /** GetResult implicit for fetching GroupThreadRow objects using plain SQL queries */
  implicit def GetResultGroupThreadRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[GroupThreadRow] = GR{
    prs => import prs._
    GroupThreadRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table group_thread. Objects of this class serve as prototypes for rows in queries. */
  class GroupThread(_tableTag: Tag) extends profile.api.Table[GroupThreadRow](_tableTag, "group_thread") {
    def * = (id, groupId, userId, rootThread, threadTitle, thread, threadDate) <> (GroupThreadRow.tupled, GroupThreadRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(groupId), Rep.Some(userId), Rep.Some(rootThread), Rep.Some(threadTitle), Rep.Some(thread), Rep.Some(threadDate))).shaped.<>({r=>import r._; _1.map(_=> GroupThreadRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column group_id SqlType(int4) */
    val groupId: Rep[Int] = column[Int]("group_id")
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column root_thread SqlType(int4) */
    val rootThread: Rep[Int] = column[Int]("root_thread")
    /** Database column thread_title SqlType(varchar), Length(200,true) */
    val threadTitle: Rep[String] = column[String]("thread_title", O.Length(200,varying=true))
    /** Database column thread SqlType(text) */
    val thread: Rep[String] = column[String]("thread")
    /** Database column thread_date SqlType(timestamp with time zone) */
    val threadDate: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("thread_date")
  }
  /** Collection-like TableQuery object for table GroupThread */
  lazy val GroupThread = new TableQuery(tag => new GroupThread(tag))

  /** Entity class storing rows of table Message
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param fromId Database column from_id SqlType(int4)
   *  @param toId Database column to_id SqlType(int4)
   *  @param message Database column message SqlType(varchar), Length(50000,true)
   *  @param dateOfMessage Database column date_of_message SqlType(timestamp with time zone) */
  case class MessageRow(id: Int, fromId: Int, toId: Int, message: String, dateOfMessage: java.sql.Timestamp)
  /** GetResult implicit for fetching MessageRow objects using plain SQL queries */
  implicit def GetResultMessageRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[MessageRow] = GR{
    prs => import prs._
    MessageRow.tupled((<<[Int], <<[Int], <<[Int], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table message. Objects of this class serve as prototypes for rows in queries. */
  class Message(_tableTag: Tag) extends profile.api.Table[MessageRow](_tableTag, "message") {
    def * = (id, fromId, toId, message, dateOfMessage) <> (MessageRow.tupled, MessageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(fromId), Rep.Some(toId), Rep.Some(message), Rep.Some(dateOfMessage))).shaped.<>({r=>import r._; _1.map(_=> MessageRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column from_id SqlType(int4) */
    val fromId: Rep[Int] = column[Int]("from_id")
    /** Database column to_id SqlType(int4) */
    val toId: Rep[Int] = column[Int]("to_id")
    /** Database column message SqlType(varchar), Length(50000,true) */
    val message: Rep[String] = column[String]("message", O.Length(50000,varying=true))
    /** Database column date_of_message SqlType(timestamp with time zone) */
    val dateOfMessage: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("date_of_message")
  }
  /** Collection-like TableQuery object for table Message */
  lazy val Message = new TableQuery(tag => new Message(tag))

  /** Entity class storing rows of table Notification
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param toId Database column to_id SqlType(int4)
   *  @param `type` Database column type SqlType(int2)
   *  @param fromId Database column from_id SqlType(int4)
   *  @param eventTime Database column event_time SqlType(time without time zone)
   *  @param fromUserName Database column from_user_name SqlType(varchar), Length(150,true)
   *  @param fromUserEmail Database column from_user_email SqlType(varchar), Length(150,true)
   *  @param fromUserProfilePic Database column from_user_profile_pic SqlType(varchar), Length(650,true) */
  case class NotificationRow(id: Int, toId: Int, `type`: Short, fromId: Int, eventTime: java.sql.Time, fromUserName: String, fromUserEmail: String, fromUserProfilePic: String)
  /** GetResult implicit for fetching NotificationRow objects using plain SQL queries */
  implicit def GetResultNotificationRow(implicit e0: GR[Int], e1: GR[Short], e2: GR[java.sql.Time], e3: GR[String]): GR[NotificationRow] = GR{
    prs => import prs._
    NotificationRow.tupled((<<[Int], <<[Int], <<[Short], <<[Int], <<[java.sql.Time], <<[String], <<[String], <<[String]))
  }
  /** Table description of table notification. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class Notification(_tableTag: Tag) extends profile.api.Table[NotificationRow](_tableTag, "notification") {
    def * = (id, toId, `type`, fromId, eventTime, fromUserName, fromUserEmail, fromUserProfilePic) <> (NotificationRow.tupled, NotificationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(toId), Rep.Some(`type`), Rep.Some(fromId), Rep.Some(eventTime), Rep.Some(fromUserName), Rep.Some(fromUserEmail), Rep.Some(fromUserProfilePic))).shaped.<>({r=>import r._; _1.map(_=> NotificationRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column to_id SqlType(int4) */
    val toId: Rep[Int] = column[Int]("to_id")
    /** Database column type SqlType(int2)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Short] = column[Short]("type")
    /** Database column from_id SqlType(int4) */
    val fromId: Rep[Int] = column[Int]("from_id")
    /** Database column event_time SqlType(time without time zone) */
    val eventTime: Rep[java.sql.Time] = column[java.sql.Time]("event_time")
    /** Database column from_user_name SqlType(varchar), Length(150,true) */
    val fromUserName: Rep[String] = column[String]("from_user_name", O.Length(150,varying=true))
    /** Database column from_user_email SqlType(varchar), Length(150,true) */
    val fromUserEmail: Rep[String] = column[String]("from_user_email", O.Length(150,varying=true))
    /** Database column from_user_profile_pic SqlType(varchar), Length(650,true) */
    val fromUserProfilePic: Rep[String] = column[String]("from_user_profile_pic", O.Length(650,varying=true))
  }
  /** Collection-like TableQuery object for table Notification */
  lazy val Notification = new TableQuery(tag => new Notification(tag))
}
