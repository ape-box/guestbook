(ns guestbook.test.db.core
  (:require [guestbook.db.core :refer [*db*] :as db]
            [guestbook.db.migrations :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [config.core :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'guestbook.db.core/*db*)
    (migrations/migrate ["migrate"])
    (f)))

(deftest test-messages
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/save-message!
               t-conn
               {:name       "Sam"
                :message    "Hello Smith"
                :timestamp  "2000-01-01T11:11:11.000-00:00"})))
    (is (= [{:name       "Sam"
            :message    "Hello Smith"
            :timestamp  "2000-01-01T11:11:11.000-00:00"}]
           (db/get-messages t-conn {})))))
