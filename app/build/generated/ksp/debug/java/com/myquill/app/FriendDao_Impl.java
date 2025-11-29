package com.myquill.app;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FriendDao_Impl implements FriendDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FriendRequest> __insertionAdapterOfFriendRequest;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfRemoveRelation;

  public FriendDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFriendRequest = new EntityInsertionAdapter<FriendRequest>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `friend_requests` (`fromUserId`,`toUserId`,`status`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FriendRequest entity) {
        statement.bindLong(1, entity.getFromUserId());
        statement.bindLong(2, entity.getToUserId());
        statement.bindString(3, entity.getStatus());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE friend_requests SET status = ? WHERE fromUserId = ? AND toUserId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemoveRelation = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM friend_requests WHERE (fromUserId = ? AND toUserId = ?) OR (fromUserId = ? AND toUserId = ?)";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final FriendRequest request, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFriendRequest.insert(request);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final long from, final long to, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, from);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, to);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object removeRelation(final long userId, final long otherId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveRelation.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, otherId);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, otherId);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, userId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRemoveRelation.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incomingRequests(final long userId,
      final Continuation<? super List<FriendRequest>> $completion) {
    final String _sql = "SELECT * FROM friend_requests WHERE toUserId = ? AND status = 'pending'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FriendRequest>>() {
      @Override
      @NonNull
      public List<FriendRequest> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFromUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromUserId");
          final int _cursorIndexOfToUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "toUserId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<FriendRequest> _result = new ArrayList<FriendRequest>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FriendRequest _item;
            final long _tmpFromUserId;
            _tmpFromUserId = _cursor.getLong(_cursorIndexOfFromUserId);
            final long _tmpToUserId;
            _tmpToUserId = _cursor.getLong(_cursorIndexOfToUserId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new FriendRequest(_tmpFromUserId,_tmpToUserId,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object outgoingRequests(final long userId,
      final Continuation<? super List<FriendRequest>> $completion) {
    final String _sql = "SELECT * FROM friend_requests WHERE fromUserId = ? AND status = 'pending'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FriendRequest>>() {
      @Override
      @NonNull
      public List<FriendRequest> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFromUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromUserId");
          final int _cursorIndexOfToUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "toUserId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<FriendRequest> _result = new ArrayList<FriendRequest>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FriendRequest _item;
            final long _tmpFromUserId;
            _tmpFromUserId = _cursor.getLong(_cursorIndexOfFromUserId);
            final long _tmpToUserId;
            _tmpToUserId = _cursor.getLong(_cursorIndexOfToUserId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new FriendRequest(_tmpFromUserId,_tmpToUserId,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object relationBetween(final long userId, final long otherId,
      final Continuation<? super FriendRequest> $completion) {
    final String _sql = "SELECT * FROM friend_requests WHERE (fromUserId = ? AND toUserId = ?) OR (fromUserId = ? AND toUserId = ?) LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, otherId);
    _argIndex = 3;
    _statement.bindLong(_argIndex, otherId);
    _argIndex = 4;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FriendRequest>() {
      @Override
      @Nullable
      public FriendRequest call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFromUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "fromUserId");
          final int _cursorIndexOfToUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "toUserId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final FriendRequest _result;
          if (_cursor.moveToFirst()) {
            final long _tmpFromUserId;
            _tmpFromUserId = _cursor.getLong(_cursorIndexOfFromUserId);
            final long _tmpToUserId;
            _tmpToUserId = _cursor.getLong(_cursorIndexOfToUserId);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new FriendRequest(_tmpFromUserId,_tmpToUserId,_tmpStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object friends(final long userId, final Continuation<? super List<User>> $completion) {
    final String _sql = "SELECT u.* FROM users u INNER JOIN friend_requests f ON ((f.fromUserId = ? AND f.toUserId = u.id) OR (f.toUserId = ? AND f.fromUserId = u.id)) WHERE f.status = 'accepted'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<User>>() {
      @Override
      @NonNull
      public List<User> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfProfileImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "profileImageUri");
          final int _cursorIndexOfGender = CursorUtil.getColumnIndexOrThrow(_cursor, "gender");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final List<User> _result = new ArrayList<User>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final User _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpFirstName;
            _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            final String _tmpLastName;
            _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpPassword;
            _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            final String _tmpProfileImageUri;
            if (_cursor.isNull(_cursorIndexOfProfileImageUri)) {
              _tmpProfileImageUri = null;
            } else {
              _tmpProfileImageUri = _cursor.getString(_cursorIndexOfProfileImageUri);
            }
            final String _tmpGender;
            if (_cursor.isNull(_cursorIndexOfGender)) {
              _tmpGender = null;
            } else {
              _tmpGender = _cursor.getString(_cursorIndexOfGender);
            }
            final Integer _tmpAge;
            if (_cursor.isNull(_cursorIndexOfAge)) {
              _tmpAge = null;
            } else {
              _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            }
            final String _tmpBio;
            if (_cursor.isNull(_cursorIndexOfBio)) {
              _tmpBio = null;
            } else {
              _tmpBio = _cursor.getString(_cursorIndexOfBio);
            }
            _item = new User(_tmpId,_tmpFirstName,_tmpLastName,_tmpUsername,_tmpEmail,_tmpPassword,_tmpProfileImageUri,_tmpGender,_tmpAge,_tmpBio);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
