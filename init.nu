buckets create  default 100
cb-env bucket default
cb-env scope _default
collections create users
query "CREATE PRIMARY INDEX ON `users`"