remove.outliers <- function(x, conf.level = 0.95)
{
  x <- x[!is.na(x)]
  del.val <- NULL
  
  while (TRUE) {
    n <- length(x)
    if (n < 3) {
      break
    }
    
    r <- range(x)
    t <- abs(r - mean(x)) / sd(x)
    q <- sqrt((n - 2) / ((n - 1) ^ 2 / t ^ 2 / n - 1))
    p <- n * pt(q, n - 2, lower.tail = FALSE)
    if (t[1] < t[2]) {
      if (p[2] < 1 - conf.level) {
        del.val <- c(del.val, r[2])
        x <- x[x != r[2]]
        next
      }
    } else {
      if (p[1] < 1 - conf.level) {
        del.val <- c(del.val, r[1])
        x <- x[x != r[1]]
        next
      }
    }
    break
  }
  return(list(x = x, del.val = del.val))
}
